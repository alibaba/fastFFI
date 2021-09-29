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
package com.alibaba.fastffi.llvm;


import com.alibaba.fastffi.CXXEnum;
import com.alibaba.fastffi.CXXEnumMap;
import com.alibaba.fastffi.CXXHead;
import com.alibaba.fastffi.FFIGen;
import com.alibaba.fastffi.FFIGetter;
import com.alibaba.fastffi.FFILibrary;
import com.alibaba.fastffi.FFITypeAlias;
import com.alibaba.fastffi.FFITypeFactory;

@FFITypeAlias("llvm::Intrinsic::ID")
public enum IntrinsicID implements CXXEnum {
    not_intrinsic(Library.INSTANCE.not_intrinsic()),
    addressofreturnaddress(Library.INSTANCE.addressofreturnaddress()),
    adjust_trampoline(Library.INSTANCE.adjust_trampoline()),
    annotation(Library.INSTANCE.annotation()),
    assume(Library.INSTANCE.assume()),
    bitreverse(Library.INSTANCE.bitreverse()),
    bswap(Library.INSTANCE.bswap()),
    call_preallocated_arg(Library.INSTANCE.call_preallocated_arg()),
    call_preallocated_setup(Library.INSTANCE.call_preallocated_setup()),
    call_preallocated_teardown(Library.INSTANCE.call_preallocated_teardown()),
    canonicalize(Library.INSTANCE.canonicalize()),
    ceil(Library.INSTANCE.ceil()),
    clear_cache(Library.INSTANCE.clear_cache()),
    codeview_annotation(Library.INSTANCE.codeview_annotation()),
    convert_from_fp16(Library.INSTANCE.convert_from_fp16()),
    convert_to_fp16(Library.INSTANCE.convert_to_fp16()),
    copysign(Library.INSTANCE.copysign()),
    coro_alloc(Library.INSTANCE.coro_alloc()),
    coro_alloca_alloc(Library.INSTANCE.coro_alloca_alloc()),
    coro_alloca_free(Library.INSTANCE.coro_alloca_free()),
    coro_alloca_get(Library.INSTANCE.coro_alloca_get()),
    coro_begin(Library.INSTANCE.coro_begin()),
    coro_destroy(Library.INSTANCE.coro_destroy()),
    coro_done(Library.INSTANCE.coro_done()),
    coro_end(Library.INSTANCE.coro_end()),
    coro_frame(Library.INSTANCE.coro_frame()),
    coro_free(Library.INSTANCE.coro_free()),
    coro_id(Library.INSTANCE.coro_id()),
    coro_id_retcon(Library.INSTANCE.coro_id_retcon()),
    coro_id_retcon_once(Library.INSTANCE.coro_id_retcon_once()),
    coro_noop(Library.INSTANCE.coro_noop()),
    coro_param(Library.INSTANCE.coro_param()),
    coro_prepare_retcon(Library.INSTANCE.coro_prepare_retcon()),
    coro_promise(Library.INSTANCE.coro_promise()),
    coro_resume(Library.INSTANCE.coro_resume()),
    coro_save(Library.INSTANCE.coro_save()),
    coro_size(Library.INSTANCE.coro_size()),
    coro_subfn_addr(Library.INSTANCE.coro_subfn_addr()),
    coro_suspend(Library.INSTANCE.coro_suspend()),
    coro_suspend_retcon(Library.INSTANCE.coro_suspend_retcon()),
    cos(Library.INSTANCE.cos()),
    ctlz(Library.INSTANCE.ctlz()),
    ctpop(Library.INSTANCE.ctpop()),
    cttz(Library.INSTANCE.cttz()),
    dbg_addr(Library.INSTANCE.dbg_addr()),
    dbg_declare(Library.INSTANCE.dbg_declare()),
    dbg_label(Library.INSTANCE.dbg_label()),
    dbg_value(Library.INSTANCE.dbg_value()),
    debugtrap(Library.INSTANCE.debugtrap()),
    donothing(Library.INSTANCE.donothing()),
    eh_dwarf_cfa(Library.INSTANCE.eh_dwarf_cfa()),
    eh_exceptioncode(Library.INSTANCE.eh_exceptioncode()),
    eh_exceptionpointer(Library.INSTANCE.eh_exceptionpointer()),
    eh_recoverfp(Library.INSTANCE.eh_recoverfp()),
    eh_return_i32(Library.INSTANCE.eh_return_i32()),
    eh_return_i64(Library.INSTANCE.eh_return_i64()),
    eh_sjlj_callsite(Library.INSTANCE.eh_sjlj_callsite()),
    eh_sjlj_functioncontext(Library.INSTANCE.eh_sjlj_functioncontext()),
    eh_sjlj_longjmp(Library.INSTANCE.eh_sjlj_longjmp()),
    eh_sjlj_lsda(Library.INSTANCE.eh_sjlj_lsda()),
    eh_sjlj_setjmp(Library.INSTANCE.eh_sjlj_setjmp()),
    eh_sjlj_setup_dispatch(Library.INSTANCE.eh_sjlj_setup_dispatch()),
    eh_typeid_for(Library.INSTANCE.eh_typeid_for()),
    eh_unwind_init(Library.INSTANCE.eh_unwind_init()),
    exp(Library.INSTANCE.exp()),
    exp2(Library.INSTANCE.exp2()),
    expect(Library.INSTANCE.expect()),
    expect_with_probability(Library.INSTANCE.expect_with_probability()),
    experimental_constrained_ceil(Library.INSTANCE.experimental_constrained_ceil()),
    experimental_constrained_cos(Library.INSTANCE.experimental_constrained_cos()),
    experimental_constrained_exp(Library.INSTANCE.experimental_constrained_exp()),
    experimental_constrained_exp2(Library.INSTANCE.experimental_constrained_exp2()),
    experimental_constrained_fadd(Library.INSTANCE.experimental_constrained_fadd()),
    experimental_constrained_fcmp(Library.INSTANCE.experimental_constrained_fcmp()),
    experimental_constrained_fcmps(Library.INSTANCE.experimental_constrained_fcmps()),
    experimental_constrained_fdiv(Library.INSTANCE.experimental_constrained_fdiv()),
    experimental_constrained_floor(Library.INSTANCE.experimental_constrained_floor()),
    experimental_constrained_fma(Library.INSTANCE.experimental_constrained_fma()),
    experimental_constrained_fmul(Library.INSTANCE.experimental_constrained_fmul()),
    experimental_constrained_fmuladd(Library.INSTANCE.experimental_constrained_fmuladd()),
    experimental_constrained_fpext(Library.INSTANCE.experimental_constrained_fpext()),
    experimental_constrained_fptosi(Library.INSTANCE.experimental_constrained_fptosi()),
    experimental_constrained_fptoui(Library.INSTANCE.experimental_constrained_fptoui()),
    experimental_constrained_fptrunc(Library.INSTANCE.experimental_constrained_fptrunc()),
    experimental_constrained_frem(Library.INSTANCE.experimental_constrained_frem()),
    experimental_constrained_fsub(Library.INSTANCE.experimental_constrained_fsub()),
    experimental_constrained_llrint(Library.INSTANCE.experimental_constrained_llrint()),
    experimental_constrained_llround(Library.INSTANCE.experimental_constrained_llround()),
    experimental_constrained_log(Library.INSTANCE.experimental_constrained_log()),
    experimental_constrained_log10(Library.INSTANCE.experimental_constrained_log10()),
    experimental_constrained_log2(Library.INSTANCE.experimental_constrained_log2()),
    experimental_constrained_lrint(Library.INSTANCE.experimental_constrained_lrint()),
    experimental_constrained_lround(Library.INSTANCE.experimental_constrained_lround()),
    experimental_constrained_maximum(Library.INSTANCE.experimental_constrained_maximum()),
    experimental_constrained_maxnum(Library.INSTANCE.experimental_constrained_maxnum()),
    experimental_constrained_minimum(Library.INSTANCE.experimental_constrained_minimum()),
    experimental_constrained_minnum(Library.INSTANCE.experimental_constrained_minnum()),
    experimental_constrained_nearbyint(Library.INSTANCE.experimental_constrained_nearbyint()),
    experimental_constrained_pow(Library.INSTANCE.experimental_constrained_pow()),
    experimental_constrained_powi(Library.INSTANCE.experimental_constrained_powi()),
    experimental_constrained_rint(Library.INSTANCE.experimental_constrained_rint()),
    experimental_constrained_round(Library.INSTANCE.experimental_constrained_round()),
    experimental_constrained_roundeven(Library.INSTANCE.experimental_constrained_roundeven()),
    experimental_constrained_sin(Library.INSTANCE.experimental_constrained_sin()),
    experimental_constrained_sitofp(Library.INSTANCE.experimental_constrained_sitofp()),
    experimental_constrained_sqrt(Library.INSTANCE.experimental_constrained_sqrt()),
    experimental_constrained_trunc(Library.INSTANCE.experimental_constrained_trunc()),
    experimental_constrained_uitofp(Library.INSTANCE.experimental_constrained_uitofp()),
    experimental_deoptimize(Library.INSTANCE.experimental_deoptimize()),
    experimental_gc_relocate(Library.INSTANCE.experimental_gc_relocate()),
    experimental_gc_result(Library.INSTANCE.experimental_gc_result()),
    experimental_gc_statepoint(Library.INSTANCE.experimental_gc_statepoint()),
    experimental_guard(Library.INSTANCE.experimental_guard()),
    experimental_patchpoint_i64(Library.INSTANCE.experimental_patchpoint_i64()),
    experimental_patchpoint_void(Library.INSTANCE.experimental_patchpoint_void()),
    experimental_stackmap(Library.INSTANCE.experimental_stackmap()),
    experimental_vector_reduce_add(Library.INSTANCE.experimental_vector_reduce_add()),
    experimental_vector_reduce_and(Library.INSTANCE.experimental_vector_reduce_and()),
    experimental_vector_reduce_fmax(Library.INSTANCE.experimental_vector_reduce_fmax()),
    experimental_vector_reduce_fmin(Library.INSTANCE.experimental_vector_reduce_fmin()),
    experimental_vector_reduce_mul(Library.INSTANCE.experimental_vector_reduce_mul()),
    experimental_vector_reduce_or(Library.INSTANCE.experimental_vector_reduce_or()),
    experimental_vector_reduce_smax(Library.INSTANCE.experimental_vector_reduce_smax()),
    experimental_vector_reduce_smin(Library.INSTANCE.experimental_vector_reduce_smin()),
    experimental_vector_reduce_umax(Library.INSTANCE.experimental_vector_reduce_umax()),
    experimental_vector_reduce_umin(Library.INSTANCE.experimental_vector_reduce_umin()),
    experimental_vector_reduce_v2_fadd(Library.INSTANCE.experimental_vector_reduce_v2_fadd()),
    experimental_vector_reduce_v2_fmul(Library.INSTANCE.experimental_vector_reduce_v2_fmul()),
    experimental_vector_reduce_xor(Library.INSTANCE.experimental_vector_reduce_xor()),
    experimental_widenable_condition(Library.INSTANCE.experimental_widenable_condition()),
    fabs(Library.INSTANCE.fabs()),
    floor(Library.INSTANCE.floor()),
    flt_rounds(Library.INSTANCE.flt_rounds()),
    fma(Library.INSTANCE.fma()),
    fmuladd(Library.INSTANCE.fmuladd()),
    frameaddress(Library.INSTANCE.frameaddress()),
    fshl(Library.INSTANCE.fshl()),
    fshr(Library.INSTANCE.fshr()),
    gcread(Library.INSTANCE.gcread()),
    gcroot(Library.INSTANCE.gcroot()),
    gcwrite(Library.INSTANCE.gcwrite()),
    get_active_lane_mask(Library.INSTANCE.get_active_lane_mask()),
    get_dynamic_area_offset(Library.INSTANCE.get_dynamic_area_offset()),
    hwasan_check_memaccess(Library.INSTANCE.hwasan_check_memaccess()),
    hwasan_check_memaccess_shortgranules(Library.INSTANCE.hwasan_check_memaccess_shortgranules()),
    icall_branch_funnel(Library.INSTANCE.icall_branch_funnel()),
    init_trampoline(Library.INSTANCE.init_trampoline()),
    instrprof_increment(Library.INSTANCE.instrprof_increment()),
    instrprof_increment_step(Library.INSTANCE.instrprof_increment_step()),
    instrprof_value_profile(Library.INSTANCE.instrprof_value_profile()),
    invariant_end(Library.INSTANCE.invariant_end()),
    invariant_start(Library.INSTANCE.invariant_start()),
    is_constant(Library.INSTANCE.is_constant()),
    launder_invariant_group(Library.INSTANCE.launder_invariant_group()),
    lifetime_end(Library.INSTANCE.lifetime_end()),
    lifetime_start(Library.INSTANCE.lifetime_start()),
    llrint(Library.INSTANCE.llrint()),
    llround(Library.INSTANCE.llround()),
    load_relative(Library.INSTANCE.load_relative()),
    localaddress(Library.INSTANCE.localaddress()),
    localescape(Library.INSTANCE.localescape()),
    localrecover(Library.INSTANCE.localrecover()),
    log(Library.INSTANCE.log()),
    log10(Library.INSTANCE.log10()),
    log2(Library.INSTANCE.log2()),
    loop_decrement(Library.INSTANCE.loop_decrement()),
    loop_decrement_reg(Library.INSTANCE.loop_decrement_reg()),
    lrint(Library.INSTANCE.lrint()),
    lround(Library.INSTANCE.lround()),
    masked_compressstore(Library.INSTANCE.masked_compressstore()),
    masked_expandload(Library.INSTANCE.masked_expandload()),
    masked_gather(Library.INSTANCE.masked_gather()),
    masked_load(Library.INSTANCE.masked_load()),
    masked_scatter(Library.INSTANCE.masked_scatter()),
    masked_store(Library.INSTANCE.masked_store()),
    matrix_column_major_load(Library.INSTANCE.matrix_column_major_load()),
    matrix_column_major_store(Library.INSTANCE.matrix_column_major_store()),
    matrix_multiply(Library.INSTANCE.matrix_multiply()),
    matrix_transpose(Library.INSTANCE.matrix_transpose()),
    maximum(Library.INSTANCE.maximum()),
    maxnum(Library.INSTANCE.maxnum()),
    memcpy(Library.INSTANCE.memcpy()),
    memcpy_element_unordered_atomic(Library.INSTANCE.memcpy_element_unordered_atomic()),
    memcpy_inline(Library.INSTANCE.memcpy_inline()),
    memmove(Library.INSTANCE.memmove()),
    memmove_element_unordered_atomic(Library.INSTANCE.memmove_element_unordered_atomic()),
    memset(Library.INSTANCE.memset()),
    memset_element_unordered_atomic(Library.INSTANCE.memset_element_unordered_atomic()),
    minimum(Library.INSTANCE.minimum()),
    minnum(Library.INSTANCE.minnum()),
    nearbyint(Library.INSTANCE.nearbyint()),
    objc_arc_annotation_bottomup_bbend(Library.INSTANCE.objc_arc_annotation_bottomup_bbend()),
    objc_arc_annotation_bottomup_bbstart(Library.INSTANCE.objc_arc_annotation_bottomup_bbstart()),
    objc_arc_annotation_topdown_bbend(Library.INSTANCE.objc_arc_annotation_topdown_bbend()),
    objc_arc_annotation_topdown_bbstart(Library.INSTANCE.objc_arc_annotation_topdown_bbstart()),
    objc_autorelease(Library.INSTANCE.objc_autorelease()),
    objc_autoreleasePoolPop(Library.INSTANCE.objc_autoreleasePoolPop()),
    objc_autoreleasePoolPush(Library.INSTANCE.objc_autoreleasePoolPush()),
    objc_autoreleaseReturnValue(Library.INSTANCE.objc_autoreleaseReturnValue()),
    objc_clang_arc_use(Library.INSTANCE.objc_clang_arc_use()),
    objc_copyWeak(Library.INSTANCE.objc_copyWeak()),
    objc_destroyWeak(Library.INSTANCE.objc_destroyWeak()),
    objc_initWeak(Library.INSTANCE.objc_initWeak()),
    objc_loadWeak(Library.INSTANCE.objc_loadWeak()),
    objc_loadWeakRetained(Library.INSTANCE.objc_loadWeakRetained()),
    objc_moveWeak(Library.INSTANCE.objc_moveWeak()),
    objc_release(Library.INSTANCE.objc_release()),
    objc_retain(Library.INSTANCE.objc_retain()),
    objc_retain_autorelease(Library.INSTANCE.objc_retain_autorelease()),
    objc_retainAutorelease(Library.INSTANCE.objc_retainAutorelease()),
    objc_retainAutoreleaseReturnValue(Library.INSTANCE.objc_retainAutoreleaseReturnValue()),
    objc_retainAutoreleasedReturnValue(Library.INSTANCE.objc_retainAutoreleasedReturnValue()),
    objc_retainBlock(Library.INSTANCE.objc_retainBlock()),
    objc_retainedObject(Library.INSTANCE.objc_retainedObject()),
    objc_storeStrong(Library.INSTANCE.objc_storeStrong()),
    objc_storeWeak(Library.INSTANCE.objc_storeWeak()),
    objc_sync_enter(Library.INSTANCE.objc_sync_enter()),
    objc_sync_exit(Library.INSTANCE.objc_sync_exit()),
    objc_unretainedObject(Library.INSTANCE.objc_unretainedObject()),
    objc_unretainedPointer(Library.INSTANCE.objc_unretainedPointer()),
    objc_unsafeClaimAutoreleasedReturnValue(Library.INSTANCE.objc_unsafeClaimAutoreleasedReturnValue()),
    objectsize(Library.INSTANCE.objectsize()),
    pcmarker(Library.INSTANCE.pcmarker()),
    pow(Library.INSTANCE.pow()),
    powi(Library.INSTANCE.powi()),
    prefetch(Library.INSTANCE.prefetch()),
    preserve_array_access_index(Library.INSTANCE.preserve_array_access_index()),
    preserve_struct_access_index(Library.INSTANCE.preserve_struct_access_index()),
    preserve_union_access_index(Library.INSTANCE.preserve_union_access_index()),
    ptr_annotation(Library.INSTANCE.ptr_annotation()),
    ptrmask(Library.INSTANCE.ptrmask()),
    read_register(Library.INSTANCE.read_register()),
    read_volatile_register(Library.INSTANCE.read_volatile_register()),
    readcyclecounter(Library.INSTANCE.readcyclecounter()),
    returnaddress(Library.INSTANCE.returnaddress()),
    rint(Library.INSTANCE.rint()),
    round(Library.INSTANCE.round()),
    roundeven(Library.INSTANCE.roundeven()),
    sadd_sat(Library.INSTANCE.sadd_sat()),
    sadd_with_overflow(Library.INSTANCE.sadd_with_overflow()),
    sdiv_fix(Library.INSTANCE.sdiv_fix()),
    sdiv_fix_sat(Library.INSTANCE.sdiv_fix_sat()),
    set_loop_iterations(Library.INSTANCE.set_loop_iterations()),
    sideeffect(Library.INSTANCE.sideeffect()),
    sin(Library.INSTANCE.sin()),
    smul_fix(Library.INSTANCE.smul_fix()),
    smul_fix_sat(Library.INSTANCE.smul_fix_sat()),
    smul_with_overflow(Library.INSTANCE.smul_with_overflow()),
    sponentry(Library.INSTANCE.sponentry()),
    sqrt(Library.INSTANCE.sqrt()),
    ssa_copy(Library.INSTANCE.ssa_copy()),
    ssub_sat(Library.INSTANCE.ssub_sat()),
    ssub_with_overflow(Library.INSTANCE.ssub_with_overflow()),
    stackguard(Library.INSTANCE.stackguard()),
    stackprotector(Library.INSTANCE.stackprotector()),
    stackrestore(Library.INSTANCE.stackrestore()),
    stacksave(Library.INSTANCE.stacksave()),
    strip_invariant_group(Library.INSTANCE.strip_invariant_group()),
    test_set_loop_iterations(Library.INSTANCE.test_set_loop_iterations()),
    thread_pointer(Library.INSTANCE.thread_pointer()),
    trap(Library.INSTANCE.trap()),
    trunc(Library.INSTANCE.trunc()),
    type_checked_load(Library.INSTANCE.type_checked_load()),
    type_test(Library.INSTANCE.type_test()),
    uadd_sat(Library.INSTANCE.uadd_sat()),
    uadd_with_overflow(Library.INSTANCE.uadd_with_overflow()),
    udiv_fix(Library.INSTANCE.udiv_fix()),
    udiv_fix_sat(Library.INSTANCE.udiv_fix_sat()),
    umul_fix(Library.INSTANCE.umul_fix()),
    umul_fix_sat(Library.INSTANCE.umul_fix_sat()),
    umul_with_overflow(Library.INSTANCE.umul_with_overflow()),
    usub_sat(Library.INSTANCE.usub_sat()),
    usub_with_overflow(Library.INSTANCE.usub_with_overflow()),
    vacopy(Library.INSTANCE.vacopy()),
    vaend(Library.INSTANCE.vaend()),
    vastart(Library.INSTANCE.vastart()),
    var_annotation(Library.INSTANCE.var_annotation()),
    vp_add(Library.INSTANCE.vp_add()),
    vp_and(Library.INSTANCE.vp_and()),
    vp_ashr(Library.INSTANCE.vp_ashr()),
    vp_lshr(Library.INSTANCE.vp_lshr()),
    vp_mul(Library.INSTANCE.vp_mul()),
    vp_or(Library.INSTANCE.vp_or()),
    vp_sdiv(Library.INSTANCE.vp_sdiv()),
    vp_shl(Library.INSTANCE.vp_shl()),
    vp_srem(Library.INSTANCE.vp_srem()),
    vp_sub(Library.INSTANCE.vp_sub()),
    vp_udiv(Library.INSTANCE.vp_udiv()),
    vp_urem(Library.INSTANCE.vp_urem()),
    vp_xor(Library.INSTANCE.vp_xor()),
    vscale(Library.INSTANCE.vscale()),
    write_register(Library.INSTANCE.write_register()),
    xray_customevent(Library.INSTANCE.xray_customevent()),
    xray_typedevent(Library.INSTANCE.xray_typedevent()),
    num_intrinsics(Library.INSTANCE.num_intrinsics()),
    unknown_intrinsics(-1)
    ;

    @FFIGen
    @CXXHead("llvm/IR/Intrinsics.h")
    @FFILibrary(value = "llvm::Intrinsic::ID", namespace = "llvm::Intrinsic")
    interface Library {
        Library INSTANCE = FFITypeFactory.getLibrary(Library.class);

        @FFIGetter
        int not_intrinsic();
        @FFIGetter
        int addressofreturnaddress();                    // llvm.addressofreturnaddress
        @FFIGetter
        int adjust_trampoline();                         // llvm.adjust.trampoline
        @FFIGetter
        int annotation();                                // llvm.annotation
        @FFIGetter
        int assume();                                    // llvm.assume
        @FFIGetter
        int bitreverse();                                // llvm.bitreverse
        @FFIGetter
        int bswap();                                     // llvm.bswap
        @FFIGetter
        int call_preallocated_arg();                     // llvm.call.preallocated.arg
        @FFIGetter
        int call_preallocated_setup();                   // llvm.call.preallocated.setup
        @FFIGetter
        int call_preallocated_teardown();                // llvm.call.preallocated.teardown
        @FFIGetter
        int canonicalize();                              // llvm.canonicalize
        @FFIGetter
        int ceil();                                      // llvm.ceil
        @FFIGetter
        int clear_cache();                               // llvm.clear_cache
        @FFIGetter
        int codeview_annotation();                       // llvm.codeview.annotation
        @FFIGetter
        int convert_from_fp16();                         // llvm.convert.from.fp16
        @FFIGetter
        int convert_to_fp16();                           // llvm.convert.to.fp16
        @FFIGetter
        int copysign();                                  // llvm.copysign
        @FFIGetter
        int coro_alloc();                                // llvm.coro.alloc
        @FFIGetter
        int coro_alloca_alloc();                         // llvm.coro.alloca.alloc
        @FFIGetter
        int coro_alloca_free();                          // llvm.coro.alloca.free
        @FFIGetter
        int coro_alloca_get();                           // llvm.coro.alloca.get
        @FFIGetter
        int coro_begin();                                // llvm.coro.begin
        @FFIGetter
        int coro_destroy();                              // llvm.coro.destroy
        @FFIGetter
        int coro_done();                                 // llvm.coro.done
        @FFIGetter
        int coro_end();                                  // llvm.coro.end
        @FFIGetter
        int coro_frame();                                // llvm.coro.frame
        @FFIGetter
        int coro_free();                                 // llvm.coro.free
        @FFIGetter
        int coro_id();                                   // llvm.coro.id
        @FFIGetter
        int coro_id_retcon();                            // llvm.coro.id.retcon
        @FFIGetter
        int coro_id_retcon_once();                       // llvm.coro.id.retcon.once
        @FFIGetter
        int coro_noop();                                 // llvm.coro.noop
        @FFIGetter
        int coro_param();                                // llvm.coro.param
        @FFIGetter
        int coro_prepare_retcon();                       // llvm.coro.prepare.retcon
        @FFIGetter
        int coro_promise();                              // llvm.coro.promise
        @FFIGetter
        int coro_resume();                               // llvm.coro.resume
        @FFIGetter
        int coro_save();                                 // llvm.coro.save
        @FFIGetter
        int coro_size();                                 // llvm.coro.size
        @FFIGetter
        int coro_subfn_addr();                           // llvm.coro.subfn.addr
        @FFIGetter
        int coro_suspend();                              // llvm.coro.suspend
        @FFIGetter
        int coro_suspend_retcon();                       // llvm.coro.suspend.retcon
        @FFIGetter
        int cos();                                       // llvm.cos
        @FFIGetter
        int ctlz();                                      // llvm.ctlz
        @FFIGetter
        int ctpop();                                     // llvm.ctpop
        @FFIGetter
        int cttz();                                      // llvm.cttz
        @FFIGetter
        int dbg_addr();                                  // llvm.dbg.addr
        @FFIGetter
        int dbg_declare();                               // llvm.dbg.declare
        @FFIGetter
        int dbg_label();                                 // llvm.dbg.label
        @FFIGetter
        int dbg_value();                                 // llvm.dbg.value
        @FFIGetter
        int debugtrap();                                 // llvm.debugtrap
        @FFIGetter
        int donothing();                                 // llvm.donothing
        @FFIGetter
        int eh_dwarf_cfa();                              // llvm.eh.dwarf.cfa
        @FFIGetter
        int eh_exceptioncode();                          // llvm.eh.exceptioncode
        @FFIGetter
        int eh_exceptionpointer();                       // llvm.eh.exceptionpointer
        @FFIGetter
        int eh_recoverfp();                              // llvm.eh.recoverfp
        @FFIGetter
        int eh_return_i32();                             // llvm.eh.return.i32
        @FFIGetter
        int eh_return_i64();                             // llvm.eh.return.i64
        @FFIGetter
        int eh_sjlj_callsite();                          // llvm.eh.sjlj.callsite
        @FFIGetter
        int eh_sjlj_functioncontext();                   // llvm.eh.sjlj.functioncontext
        @FFIGetter
        int eh_sjlj_longjmp();                           // llvm.eh.sjlj.longjmp
        @FFIGetter
        int eh_sjlj_lsda();                              // llvm.eh.sjlj.lsda
        @FFIGetter
        int eh_sjlj_setjmp();                            // llvm.eh.sjlj.setjmp
        @FFIGetter
        int eh_sjlj_setup_dispatch();                    // llvm.eh.sjlj.setup.dispatch
        @FFIGetter
        int eh_typeid_for();                             // llvm.eh.typeid.for
        @FFIGetter
        int eh_unwind_init();                            // llvm.eh.unwind.init
        @FFIGetter
        int exp();                                       // llvm.exp
        @FFIGetter
        int exp2();                                      // llvm.exp2
        @FFIGetter
        int expect();                                    // llvm.expect
        @FFIGetter
        int expect_with_probability();                   // llvm.expect.with.probability
        @FFIGetter
        int experimental_constrained_ceil();             // llvm.experimental.constrained.ceil
        @FFIGetter
        int experimental_constrained_cos();              // llvm.experimental.constrained.cos
        @FFIGetter
        int experimental_constrained_exp();              // llvm.experimental.constrained.exp
        @FFIGetter
        int experimental_constrained_exp2();             // llvm.experimental.constrained.exp2
        @FFIGetter
        int experimental_constrained_fadd();             // llvm.experimental.constrained.fadd
        @FFIGetter
        int experimental_constrained_fcmp();             // llvm.experimental.constrained.fcmp
        @FFIGetter
        int experimental_constrained_fcmps();            // llvm.experimental.constrained.fcmps
        @FFIGetter
        int experimental_constrained_fdiv();             // llvm.experimental.constrained.fdiv
        @FFIGetter
        int experimental_constrained_floor();            // llvm.experimental.constrained.floor
        @FFIGetter
        int experimental_constrained_fma();              // llvm.experimental.constrained.fma
        @FFIGetter
        int experimental_constrained_fmul();             // llvm.experimental.constrained.fmul
        @FFIGetter
        int experimental_constrained_fmuladd();          // llvm.experimental.constrained.fmuladd
        @FFIGetter
        int experimental_constrained_fpext();            // llvm.experimental.constrained.fpext
        @FFIGetter
        int experimental_constrained_fptosi();           // llvm.experimental.constrained.fptosi
        @FFIGetter
        int experimental_constrained_fptoui();           // llvm.experimental.constrained.fptoui
        @FFIGetter
        int experimental_constrained_fptrunc();          // llvm.experimental.constrained.fptrunc
        @FFIGetter
        int experimental_constrained_frem();             // llvm.experimental.constrained.frem
        @FFIGetter
        int experimental_constrained_fsub();             // llvm.experimental.constrained.fsub
        @FFIGetter
        int experimental_constrained_llrint();           // llvm.experimental.constrained.llrint
        @FFIGetter
        int experimental_constrained_llround();          // llvm.experimental.constrained.llround
        @FFIGetter
        int experimental_constrained_log();              // llvm.experimental.constrained.log
        @FFIGetter
        int experimental_constrained_log10();            // llvm.experimental.constrained.log10
        @FFIGetter
        int experimental_constrained_log2();             // llvm.experimental.constrained.log2
        @FFIGetter
        int experimental_constrained_lrint();            // llvm.experimental.constrained.lrint
        @FFIGetter
        int experimental_constrained_lround();           // llvm.experimental.constrained.lround
        @FFIGetter
        int experimental_constrained_maximum();          // llvm.experimental.constrained.maximum
        @FFIGetter
        int experimental_constrained_maxnum();           // llvm.experimental.constrained.maxnum
        @FFIGetter
        int experimental_constrained_minimum();          // llvm.experimental.constrained.minimum
        @FFIGetter
        int experimental_constrained_minnum();           // llvm.experimental.constrained.minnum
        @FFIGetter
        int experimental_constrained_nearbyint();        // llvm.experimental.constrained.nearbyint
        @FFIGetter
        int experimental_constrained_pow();              // llvm.experimental.constrained.pow
        @FFIGetter
        int experimental_constrained_powi();             // llvm.experimental.constrained.powi
        @FFIGetter
        int experimental_constrained_rint();             // llvm.experimental.constrained.rint
        @FFIGetter
        int experimental_constrained_round();            // llvm.experimental.constrained.round
        @FFIGetter
        int experimental_constrained_roundeven();        // llvm.experimental.constrained.roundeven
        @FFIGetter
        int experimental_constrained_sin();              // llvm.experimental.constrained.sin
        @FFIGetter
        int experimental_constrained_sitofp();           // llvm.experimental.constrained.sitofp
        @FFIGetter
        int experimental_constrained_sqrt();             // llvm.experimental.constrained.sqrt
        @FFIGetter
        int experimental_constrained_trunc();            // llvm.experimental.constrained.trunc
        @FFIGetter
        int experimental_constrained_uitofp();           // llvm.experimental.constrained.uitofp
        @FFIGetter
        int experimental_deoptimize();                   // llvm.experimental.deoptimize
        @FFIGetter
        int experimental_gc_relocate();                  // llvm.experimental.gc.relocate
        @FFIGetter
        int experimental_gc_result();                    // llvm.experimental.gc.result
        @FFIGetter
        int experimental_gc_statepoint();                // llvm.experimental.gc.statepoint
        @FFIGetter
        int experimental_guard();                        // llvm.experimental.guard
        @FFIGetter
        int experimental_patchpoint_i64();               // llvm.experimental.patchpoint.i64
        @FFIGetter
        int experimental_patchpoint_void();              // llvm.experimental.patchpoint.void
        @FFIGetter
        int experimental_stackmap();                     // llvm.experimental.stackmap
        @FFIGetter
        int experimental_vector_reduce_add();            // llvm.experimental.vector.reduce.add
        @FFIGetter
        int experimental_vector_reduce_and();            // llvm.experimental.vector.reduce.and
        @FFIGetter
        int experimental_vector_reduce_fmax();           // llvm.experimental.vector.reduce.fmax
        @FFIGetter
        int experimental_vector_reduce_fmin();           // llvm.experimental.vector.reduce.fmin
        @FFIGetter
        int experimental_vector_reduce_mul();            // llvm.experimental.vector.reduce.mul
        @FFIGetter
        int experimental_vector_reduce_or();             // llvm.experimental.vector.reduce.or
        @FFIGetter
        int experimental_vector_reduce_smax();           // llvm.experimental.vector.reduce.smax
        @FFIGetter
        int experimental_vector_reduce_smin();           // llvm.experimental.vector.reduce.smin
        @FFIGetter
        int experimental_vector_reduce_umax();           // llvm.experimental.vector.reduce.umax
        @FFIGetter
        int experimental_vector_reduce_umin();           // llvm.experimental.vector.reduce.umin
        @FFIGetter
        int experimental_vector_reduce_v2_fadd();        // llvm.experimental.vector.reduce.v2.fadd
        @FFIGetter
        int experimental_vector_reduce_v2_fmul();        // llvm.experimental.vector.reduce.v2.fmul
        @FFIGetter
        int experimental_vector_reduce_xor();            // llvm.experimental.vector.reduce.xor
        @FFIGetter
        int experimental_widenable_condition();          // llvm.experimental.widenable.condition
        @FFIGetter
        int fabs();                                      // llvm.fabs
        @FFIGetter
        int floor();                                     // llvm.floor
        @FFIGetter
        int flt_rounds();                                // llvm.flt.rounds
        @FFIGetter
        int fma();                                       // llvm.fma
        @FFIGetter
        int fmuladd();                                   // llvm.fmuladd
        @FFIGetter
        int frameaddress();                              // llvm.frameaddress
        @FFIGetter
        int fshl();                                      // llvm.fshl
        @FFIGetter
        int fshr();                                      // llvm.fshr
        @FFIGetter
        int gcread();                                    // llvm.gcread
        @FFIGetter
        int gcroot();                                    // llvm.gcroot
        @FFIGetter
        int gcwrite();                                   // llvm.gcwrite
        @FFIGetter
        int get_active_lane_mask();                      // llvm.get.active.lane.mask
        @FFIGetter
        int get_dynamic_area_offset();                   // llvm.get.dynamic.area.offset
        @FFIGetter
        int hwasan_check_memaccess();                    // llvm.hwasan.check.memaccess
        @FFIGetter
        int hwasan_check_memaccess_shortgranules();      // llvm.hwasan.check.memaccess.shortgranules
        @FFIGetter
        int icall_branch_funnel();                       // llvm.icall.branch.funnel
        @FFIGetter
        int init_trampoline();                           // llvm.init.trampoline
        @FFIGetter
        int instrprof_increment();                       // llvm.instrprof.increment
        @FFIGetter
        int instrprof_increment_step();                  // llvm.instrprof.increment.step
        @FFIGetter
        int instrprof_value_profile();                   // llvm.instrprof.value.profile
        @FFIGetter
        int invariant_end();                             // llvm.invariant.end
        @FFIGetter
        int invariant_start();                           // llvm.invariant.start
        @FFIGetter
        int is_constant();                               // llvm.is.constant
        @FFIGetter
        int launder_invariant_group();                   // llvm.launder.invariant.group
        @FFIGetter
        int lifetime_end();                              // llvm.lifetime.end
        @FFIGetter
        int lifetime_start();                            // llvm.lifetime.start
        @FFIGetter
        int llrint();                                    // llvm.llrint
        @FFIGetter
        int llround();                                   // llvm.llround
        @FFIGetter
        int load_relative();                             // llvm.load.relative
        @FFIGetter
        int localaddress();                              // llvm.localaddress
        @FFIGetter
        int localescape();                               // llvm.localescape
        @FFIGetter
        int localrecover();                              // llvm.localrecover
        @FFIGetter
        int log();                                       // llvm.log
        @FFIGetter
        int log10();                                     // llvm.log10
        @FFIGetter
        int log2();                                      // llvm.log2
        @FFIGetter
        int loop_decrement();                            // llvm.loop.decrement
        @FFIGetter
        int loop_decrement_reg();                        // llvm.loop.decrement.reg
        @FFIGetter
        int lrint();                                     // llvm.lrint
        @FFIGetter
        int lround();                                    // llvm.lround
        @FFIGetter
        int masked_compressstore();                      // llvm.masked.compressstore
        @FFIGetter
        int masked_expandload();                         // llvm.masked.expandload
        @FFIGetter
        int masked_gather();                             // llvm.masked.gather
        @FFIGetter
        int masked_load();                               // llvm.masked.load
        @FFIGetter
        int masked_scatter();                            // llvm.masked.scatter
        @FFIGetter
        int masked_store();                              // llvm.masked.store
        @FFIGetter
        int matrix_column_major_load();                  // llvm.matrix.column.major.load
        @FFIGetter
        int matrix_column_major_store();                 // llvm.matrix.column.major.store
        @FFIGetter
        int matrix_multiply();                           // llvm.matrix.multiply
        @FFIGetter
        int matrix_transpose();                          // llvm.matrix.transpose
        @FFIGetter
        int maximum();                                   // llvm.maximum
        @FFIGetter
        int maxnum();                                    // llvm.maxnum
        @FFIGetter
        int memcpy();                                    // llvm.memcpy
        @FFIGetter
        int memcpy_element_unordered_atomic();           // llvm.memcpy.element.unordered.atomic
        @FFIGetter
        int memcpy_inline();                             // llvm.memcpy.inline
        @FFIGetter
        int memmove();                                   // llvm.memmove
        @FFIGetter
        int memmove_element_unordered_atomic();          // llvm.memmove.element.unordered.atomic
        @FFIGetter
        int memset();                                    // llvm.memset
        @FFIGetter
        int memset_element_unordered_atomic();           // llvm.memset.element.unordered.atomic
        @FFIGetter
        int minimum();                                   // llvm.minimum
        @FFIGetter
        int minnum();                                    // llvm.minnum
        @FFIGetter
        int nearbyint();                                 // llvm.nearbyint
        @FFIGetter
        int objc_arc_annotation_bottomup_bbend();        // llvm.objc.arc.annotation.bottomup.bbend
        @FFIGetter
        int objc_arc_annotation_bottomup_bbstart();      // llvm.objc.arc.annotation.bottomup.bbstart
        @FFIGetter
        int objc_arc_annotation_topdown_bbend();         // llvm.objc.arc.annotation.topdown.bbend
        @FFIGetter
        int objc_arc_annotation_topdown_bbstart();       // llvm.objc.arc.annotation.topdown.bbstart
        @FFIGetter
        int objc_autorelease();                          // llvm.objc.autorelease
        @FFIGetter
        int objc_autoreleasePoolPop();                   // llvm.objc.autoreleasePoolPop
        @FFIGetter
        int objc_autoreleasePoolPush();                  // llvm.objc.autoreleasePoolPush
        @FFIGetter
        int objc_autoreleaseReturnValue();               // llvm.objc.autoreleaseReturnValue
        @FFIGetter
        int objc_clang_arc_use();                        // llvm.objc.clang.arc.use
        @FFIGetter
        int objc_copyWeak();                             // llvm.objc.copyWeak
        @FFIGetter
        int objc_destroyWeak();                          // llvm.objc.destroyWeak
        @FFIGetter
        int objc_initWeak();                             // llvm.objc.initWeak
        @FFIGetter
        int objc_loadWeak();                             // llvm.objc.loadWeak
        @FFIGetter
        int objc_loadWeakRetained();                     // llvm.objc.loadWeakRetained
        @FFIGetter
        int objc_moveWeak();                             // llvm.objc.moveWeak
        @FFIGetter
        int objc_release();                              // llvm.objc.release
        @FFIGetter
        int objc_retain();                               // llvm.objc.retain
        @FFIGetter
        int objc_retain_autorelease();                   // llvm.objc.retain.autorelease
        @FFIGetter
        int objc_retainAutorelease();                    // llvm.objc.retainAutorelease
        @FFIGetter
        int objc_retainAutoreleaseReturnValue();         // llvm.objc.retainAutoreleaseReturnValue
        @FFIGetter
        int objc_retainAutoreleasedReturnValue();        // llvm.objc.retainAutoreleasedReturnValue
        @FFIGetter
        int objc_retainBlock();                          // llvm.objc.retainBlock
        @FFIGetter
        int objc_retainedObject();                       // llvm.objc.retainedObject
        @FFIGetter
        int objc_storeStrong();                          // llvm.objc.storeStrong
        @FFIGetter
        int objc_storeWeak();                            // llvm.objc.storeWeak
        @FFIGetter
        int objc_sync_enter();                           // llvm.objc.sync.enter
        @FFIGetter
        int objc_sync_exit();                            // llvm.objc.sync.exit
        @FFIGetter
        int objc_unretainedObject();                     // llvm.objc.unretainedObject
        @FFIGetter
        int objc_unretainedPointer();                    // llvm.objc.unretainedPointer
        @FFIGetter
        int objc_unsafeClaimAutoreleasedReturnValue();   // llvm.objc.unsafeClaimAutoreleasedReturnValue
        @FFIGetter
        int objectsize();                                // llvm.objectsize
        @FFIGetter
        int pcmarker();                                  // llvm.pcmarker
        @FFIGetter
        int pow();                                       // llvm.pow
        @FFIGetter
        int powi();                                      // llvm.powi
        @FFIGetter
        int prefetch();                                  // llvm.prefetch
        @FFIGetter
        int preserve_array_access_index();               // llvm.preserve.array.access.index
        @FFIGetter
        int preserve_struct_access_index();              // llvm.preserve.struct.access.index
        @FFIGetter
        int preserve_union_access_index();               // llvm.preserve.union.access.index
        @FFIGetter
        int ptr_annotation();                            // llvm.ptr.annotation
        @FFIGetter
        int ptrmask();                                   // llvm.ptrmask
        @FFIGetter
        int read_register();                             // llvm.read_register
        @FFIGetter
        int read_volatile_register();                    // llvm.read_volatile_register
        @FFIGetter
        int readcyclecounter();                          // llvm.readcyclecounter
        @FFIGetter
        int returnaddress();                             // llvm.returnaddress
        @FFIGetter
        int rint();                                      // llvm.rint
        @FFIGetter
        int round();                                     // llvm.round
        @FFIGetter
        int roundeven();                                 // llvm.roundeven
        @FFIGetter
        int sadd_sat();                                  // llvm.sadd.sat
        @FFIGetter
        int sadd_with_overflow();                        // llvm.sadd.with.overflow
        @FFIGetter
        int sdiv_fix();                                  // llvm.sdiv.fix
        @FFIGetter
        int sdiv_fix_sat();                              // llvm.sdiv.fix.sat
        @FFIGetter
        int set_loop_iterations();                       // llvm.set.loop.iterations
        @FFIGetter
        int sideeffect();                                // llvm.sideeffect
        @FFIGetter
        int sin();                                       // llvm.sin
        @FFIGetter
        int smul_fix();                                  // llvm.smul.fix
        @FFIGetter
        int smul_fix_sat();                              // llvm.smul.fix.sat
        @FFIGetter
        int smul_with_overflow();                        // llvm.smul.with.overflow
        @FFIGetter
        int sponentry();                                 // llvm.sponentry
        @FFIGetter
        int sqrt();                                      // llvm.sqrt
        @FFIGetter
        int ssa_copy();                                  // llvm.ssa.copy
        @FFIGetter
        int ssub_sat();                                  // llvm.ssub.sat
        @FFIGetter
        int ssub_with_overflow();                        // llvm.ssub.with.overflow
        @FFIGetter
        int stackguard();                                // llvm.stackguard
        @FFIGetter
        int stackprotector();                            // llvm.stackprotector
        @FFIGetter
        int stackrestore();                              // llvm.stackrestore
        @FFIGetter
        int stacksave();                                 // llvm.stacksave
        @FFIGetter
        int strip_invariant_group();                     // llvm.strip.invariant.group
        @FFIGetter
        int test_set_loop_iterations();                  // llvm.test.set.loop.iterations
        @FFIGetter
        int thread_pointer();                            // llvm.thread.pointer
        @FFIGetter
        int trap();                                      // llvm.trap
        @FFIGetter
        int trunc();                                     // llvm.trunc
        @FFIGetter
        int type_checked_load();                         // llvm.type.checked.load
        @FFIGetter
        int type_test();                                 // llvm.type.test
        @FFIGetter
        int uadd_sat();                                  // llvm.uadd.sat
        @FFIGetter
        int uadd_with_overflow();                        // llvm.uadd.with.overflow
        @FFIGetter
        int udiv_fix();                                  // llvm.udiv.fix
        @FFIGetter
        int udiv_fix_sat();                              // llvm.udiv.fix.sat
        @FFIGetter
        int umul_fix();                                  // llvm.umul.fix
        @FFIGetter
        int umul_fix_sat();                              // llvm.umul.fix.sat
        @FFIGetter
        int umul_with_overflow();                        // llvm.umul.with.overflow
        @FFIGetter
        int usub_sat();                                  // llvm.usub.sat
        @FFIGetter
        int usub_with_overflow();                        // llvm.usub.with.overflow
        @FFIGetter
        int vacopy();                                    // llvm.va_copy
        @FFIGetter
        int vaend();                                     // llvm.va_end
        @FFIGetter
        int vastart();                                   // llvm.va_start
        @FFIGetter
        int var_annotation();                            // llvm.var.annotation
        @FFIGetter
        int vp_add();                                    // llvm.vp.add
        @FFIGetter
        int vp_and();                                    // llvm.vp.and
        @FFIGetter
        int vp_ashr();                                   // llvm.vp.ashr
        @FFIGetter
        int vp_lshr();                                   // llvm.vp.lshr
        @FFIGetter
        int vp_mul();                                    // llvm.vp.mul
        @FFIGetter
        int vp_or();                                     // llvm.vp.or
        @FFIGetter
        int vp_sdiv();                                   // llvm.vp.sdiv
        @FFIGetter
        int vp_shl();                                    // llvm.vp.shl
        @FFIGetter
        int vp_srem();                                   // llvm.vp.srem
        @FFIGetter
        int vp_sub();                                    // llvm.vp.sub
        @FFIGetter
        int vp_udiv();                                   // llvm.vp.udiv
        @FFIGetter
        int vp_urem();                                   // llvm.vp.urem
        @FFIGetter
        int vp_xor();                                    // llvm.vp.xor
        @FFIGetter
        int vscale();                                    // llvm.vscale
        @FFIGetter
        int write_register();                            // llvm.write_register
        @FFIGetter
        int xray_customevent();                          // llvm.xray.customevent
        @FFIGetter
        int xray_typedevent();                           // llvm.xray.typedevent
        @FFIGetter
        int num_intrinsics();
    }

    int value;
    IntrinsicID(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    public static CXXEnumMap<IntrinsicID> map = new CXXEnumMap<>(values());
}
