/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.fastffi.llvm4jni;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LSTORE;

public class InstructionOptimizer {


    public static String insnToString(Printer printer, TraceMethodVisitor mp, AbstractInsnNode insn){
        insn.accept(mp);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }

    public static void printInsnList(InsnList insnList) {
        Printer printer = new Textifier();
        TraceMethodVisitor mp = new TraceMethodVisitor(printer);
        AbstractInsnNode current = insnList.getFirst();
        while (current != null) {
            String content = insnToString(printer, mp, current).trim();
            if (current instanceof LabelNode) {
                Logger.info(content);
            } else {
                Logger.info("    %s", content);
            }
            current = current.getNext();
        }
    }

    static InsnList toInsnList(List<AbstractInsnNode> instructions) {
        InsnList insnList = new InsnList();
        for (AbstractInsnNode insn : instructions) {
            insnList.add(insn);
        }
        sanityCheck(insnList);
        return optimize(insnList);
    }

    static void sanityCheck(InsnList insnList) {
        for (AbstractInsnNode current = insnList.getFirst();
             current != null;
             current = current.getNext()) {
            if (current instanceof JumpInsnNode) {
                LabelNode labelNode = ((JumpInsnNode) current).label;
                if (!insnList.contains(labelNode)) {
                    throw new IllegalStateException("Oops: ");
                }
            }
        }
    }

    /**
     * BitCast/PtrToInt/IntToPtr will first load the value and then write the value.
     * Remove any Load N; Store N; pairs;
     * @param input
     */
    static void removeLoadStoreLocal(InsnList input) {
        AbstractInsnNode current = input.getFirst();
        AbstractInsnNode previous = null;
        while (current != null) {
            int local = checkLoadStorePair(previous, current);
            if (local != -1) {
                input.remove(previous);
                previous = current.getNext();
                input.remove(current);
                current = previous;
            }

            previous = current;
            current = current.getNext();
        }
    }

    /**
     * By default, all values are assumed to be used by multiple usages.
     * So, there are plenty of STORE N; Load N; pairs.
     * Remove all pairs that N only be used by those two instructions.
     * @param input
     */
    static void removeStoreLoadLocal(InsnList input) {
        AbstractInsnNode current = input.getFirst();
        AbstractInsnNode previous = null;
        Map<Integer, List<VarInsnNode>> localToInsnNodes = new HashMap<>();
        List<VarInsnNode> storeList = new ArrayList<>();
        while (current != null) {
            if (current instanceof VarInsnNode) {
                VarInsnNode varInsnNode = (VarInsnNode) current;
                Utils.addToMapList(localToInsnNodes, varInsnNode.var, varInsnNode);
            }
            int local = checkStoreLoadPair(previous, current);
            if (local != -1) {
                storeList.add((VarInsnNode) previous);
            }

            previous = current;
            current = current.getNext();
        }
        for (VarInsnNode varInsnNode : storeList) {
            List<VarInsnNode> use = localToInsnNodes.get(varInsnNode.var);
            if (use.size() == 2) {
                VarInsnNode store = use.get(0);
                VarInsnNode load = use.get(1);
                if (varInsnNode == store
                        && store.getNext() == load && load.getPrevious() == store
                        && checkStoreLoadPair(store, load) == varInsnNode.var) {
                    input.remove(store);
                    input.remove(load);
                }
            }
        }
    }

    private static int checkStoreLoadPair(AbstractInsnNode previous, AbstractInsnNode current) {
        if (previous instanceof VarInsnNode && current instanceof VarInsnNode) {
            VarInsnNode prev = (VarInsnNode) previous;
            VarInsnNode curr = (VarInsnNode) current;
            if (prev.var != curr.var) {
                return -1;
            }
            switch (prev.getOpcode()) {
                case ASTORE:
                    return curr.getOpcode() == ALOAD ? prev.var : -1;
                case ISTORE:
                    return curr.getOpcode() == ILOAD ? prev.var : -1;
                case FSTORE:
                    return curr.getOpcode() == FLOAD ? prev.var : -1;
                case LSTORE:
                    return curr.getOpcode() == LLOAD ? prev.var : -1;
                case DSTORE:
                    return curr.getOpcode() == DLOAD ? prev.var : -1;
                default:
                    break;
            }
        }
        return -1;
    }

    private static int checkLoadStorePair(AbstractInsnNode previous, AbstractInsnNode current) {
        if (previous instanceof VarInsnNode && current instanceof VarInsnNode) {
            VarInsnNode prev = (VarInsnNode) previous;
            VarInsnNode curr = (VarInsnNode) current;
            if (prev.var != curr.var) {
                return -1;
            }
            switch (prev.getOpcode()) {
                case ALOAD:
                    return curr.getOpcode() == ASTORE ? prev.var : -1;
                case ILOAD:
                    return curr.getOpcode() == ISTORE ? prev.var : -1;
                case FLOAD:
                    return curr.getOpcode() == FSTORE ? prev.var : -1;
                case LLOAD:
                    return curr.getOpcode() == LSTORE ? prev.var : -1;
                case DLOAD:
                    return curr.getOpcode() == DSTORE ? prev.var : -1;
                default:
                    break;
            }
        }
        return -1;
    }

    /**
     *         23: ifne          30
     *         26: iconst_1
     *         27: goto          31
     *         label1
     *         30: iconst_0
     *         label2
     *         31: ifne          37
     *         34: goto          544
     *         label 3
     *         37: lload_0
     *
     *         can be simplifed into
     *
     *         ifne 544
     *         lload_0
     * @param input
     */
    static void simplifyIF(InsnList input) {
        AbstractInsnNode current = input.getFirst();
        while (current != null) {
            current = doSimplifyIF(current, input);
            current = current.getNext();
        }
    }

    static void removeRedundantGoto(InsnList input) {
        Map<LabelNode, List<AbstractInsnNode>> reverseMap = buildReverseMapping(input);
        AbstractInsnNode current = input.getFirst();
        while (current != null) {
            if (current instanceof LabelNode) {
                AbstractInsnNode prev = current.getPrevious();
                if (prev instanceof JumpInsnNode) {
                    JumpInsnNode jump = (JumpInsnNode) prev;
                    if (jump.getOpcode() == Opcodes.GOTO && jump.label == current) {
                        input.remove(jump);
                        List<AbstractInsnNode> jumps = reverseMap.get(current);
                        if (jumps.size() == 1 && jumps.contains(jump)) {
                            LabelNode labelNode = (LabelNode) current;
                            current = current.getNext();
                            input.remove(labelNode);
                        }
                    }
                }
            }
            current = current.getNext();
        }
    }

    /**
     * No labels for the exception handlers
     * @param insnList
     * @return
     */
    static Map<LabelNode, List<AbstractInsnNode>> buildReverseMapping(InsnList insnList) {
        Map<LabelNode, List<AbstractInsnNode>> map = new HashMap<>();
        for (AbstractInsnNode current = insnList.getFirst();
             current != null;
             current = current.getNext()) {
            if (current instanceof JumpInsnNode) {
                LabelNode labelNode = ((JumpInsnNode) current).label;
                Utils.addToMapList(map, labelNode, current);
            } else if (current instanceof LookupSwitchInsnNode) {
                LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode) current;
                Utils.addToMapList(map, lookupSwitchInsnNode.dflt, current);
                for (LabelNode labelNode : lookupSwitchInsnNode.labels) {
                    Utils.addToMapList(map, labelNode, current);
                }
            } else if (current instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode) current;
                Utils.addToMapList(map, tableSwitchInsnNode.dflt, current);
                for (LabelNode labelNode : tableSwitchInsnNode.labels) {
                    Utils.addToMapList(map, labelNode, current);
                }
            } else if (current instanceof LineNumberNode) {
                throw new IllegalStateException("Oops: we do not generate LineNumberNode");
            }
        }
        return map;
    }

    /**
     * # ifxx L1
     * # iconst0
     * # goto L2
     * # L1
     * # iconst1
     * # L2
     * # ifeq L3
     * # goto L4
     * @param current
     * @param input
     * @return
     */
    private static AbstractInsnNode doSimplifyIF(AbstractInsnNode current, InsnList input) {
        Map<LabelNode, List<AbstractInsnNode>> reverseMap = buildReverseMapping(input);
        AbstractInsnNode inputNode = current;
        if (!(current instanceof JumpInsnNode)) {
            return inputNode;
        }
        JumpInsnNode firstIF = (JumpInsnNode) current;
        current = current.getNext();

        if (current == null || current.getOpcode() != Opcodes.ICONST_0) {
            return inputNode;
        }
        InsnNode iconst0 = (InsnNode) current;
        current = current.getNext();

        if (current == null || current.getOpcode() != Opcodes.GOTO) {
            return inputNode;
        }
        JumpInsnNode firstGoto = (JumpInsnNode) current;
        current = current.getNext();

        if (!(current instanceof LabelNode)) {
            return inputNode;
        }
        LabelNode firstLabel = (LabelNode) current;
        if (firstIF.label != firstLabel) {
            return inputNode;
        }
        {
            List<AbstractInsnNode> jumps = reverseMap.get(firstLabel);
            if (jumps.size() != 1 || !jumps.contains(firstIF)) {
                return inputNode;
            }
        }
        current = current.getNext();

        if (current == null || current.getOpcode() != Opcodes.ICONST_1) {
            return inputNode;
        }
        InsnNode iconst1 = (InsnNode) current;
        current = current.getNext();

        if (!(current instanceof LabelNode)) {
            return inputNode;
        }
        LabelNode secondLabel = (LabelNode) current;
        if (firstGoto.label != secondLabel) {
            return inputNode;
        }
        {
            List<AbstractInsnNode> jumps = reverseMap.get(secondLabel);
            if (jumps.size() != 1 || !jumps.contains(firstGoto)) {
                return inputNode;
            }
        }
        current = current.getNext();

        // If firstIf is true, then top is const1, then secondIf is true
        // then we can jump from firstIf to the target of secondIf
        if (current == null || current.getOpcode() != Opcodes.IFNE) {
            return inputNode;
        }
        JumpInsnNode secondIf = (JumpInsnNode) current;

        /**
         * # ifxx L1
         * # iconst0
         * # goto L2
         * # L1
         * # iconst1
         * # L2
         * # ifNE L3
         */
        firstIF.label = secondIf.label;
        input.remove(iconst0);
        input.remove(firstGoto);
        input.remove(firstLabel);
        input.remove(iconst1);
        input.remove(secondLabel);
        input.remove(secondIf);
        return inputNode;
    }

    static InsnList optimize(InsnList input) {
        sanityCheck(input);
        removeRedundantGoto(input);
        sanityCheck(input);
        removeLoadStoreLocal(input);
        sanityCheck(input);
        removeStoreLoadLocal(input);
        sanityCheck(input);
        simplifyIF(input);
        sanityCheck(input);
        return input;
    }

    static class LocalIndex {
        int index;
        int size; // 1 or 2

        int order;
        int newIndex;
        public LocalIndex(int index, int size) {
            this.index = index;
            this.size = size;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalIndex that = (LocalIndex) o;
            return index == that.index && size == that.size;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, size);
        }
    }

    static int getArgumentSize(MethodNode node) {
        int all = Type.getArgumentsAndReturnSizes(node.desc);
        all = all >> 2; // remove return size
        if (Modifier.isStatic(node.access)) {
            all --;
        }
        return all;
    }

    public static void reallocateVarIndex(MethodNode methodNode) {
        int argumentSize = getArgumentSize(methodNode);
        InsnList input = methodNode.instructions;
        Map<LocalIndex, LocalIndex> indice = new HashMap<>();
        for (AbstractInsnNode current = input.getFirst(); current != null; current = current.getNext()) {
            if (current instanceof VarInsnNode) {
                int size;
                int index = ((VarInsnNode) current).var;
                if (index < argumentSize) {
                    continue; // skip arguments
                }
                switch (current.getOpcode()) {
                    case ALOAD:
                    case ILOAD:
                    case FLOAD:
                    case LLOAD:
                    case DLOAD:
                        continue;
                    case ASTORE:
                    case ISTORE:
                    case FSTORE:
                        size = 1;
                        break;
                    case LSTORE:
                    case DSTORE:
                        size = 2;
                        break;
                    default:
                        throw new IllegalStateException("oops: not a supported VarInsnNode: " + current.getOpcode());
                }
                LocalIndex key = new LocalIndex(index, size);
                if (!indice.containsKey(key)) {
                    key.order = indice.size();
                    indice.put(key, key);
                }
            }
        }
        if (indice.isEmpty()) {
            return;
        }
        List<LocalIndex> list = new ArrayList<>(indice.values());
        Collections.sort(list, new Comparator<LocalIndex>() {
            @Override
            public int compare(LocalIndex o1, LocalIndex o2) {
                return o1.order - o2.order;
            }
        });
        int begin = argumentSize;
        for (LocalIndex index : list) {
            index.newIndex = begin;
            begin += index.size;
        }
        for (AbstractInsnNode current = input.getFirst(); current != null; current = current.getNext()) {
            if (current instanceof VarInsnNode) {
                int size;
                VarInsnNode varInsnNode = (VarInsnNode) current;
                int index = varInsnNode.var;
                if (index < argumentSize) {
                    continue; // skip arguments
                }
                switch (current.getOpcode()) {
                    case ALOAD:
                    case ILOAD:
                    case FLOAD:
                    case ASTORE:
                    case ISTORE:
                    case FSTORE:
                        size = 1;
                        break;
                    case LLOAD:
                    case DLOAD:
                    case LSTORE:
                    case DSTORE:
                        size = 2;
                        break;
                    default:
                        throw new IllegalStateException("Oops: not a supported VarInsnNode: " + current.getOpcode());
                }
                LocalIndex key = new LocalIndex(varInsnNode.var, size);
                LocalIndex value = indice.get(key);
                if (value == null) {
                    throw new IllegalStateException("Oops: a load does not have a store");
                }
                if (argumentSize != 0 && value.newIndex == 0) {
                    throw new IllegalStateException("Oops:");
                }
                varInsnNode.var = value.newIndex;
            }
        }
    }
}
