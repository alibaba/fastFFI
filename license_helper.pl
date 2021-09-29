#!/usr/bin/perl 
# Copyright 1999-2021 Alibaba Group Holding Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
use strict;
use utf8;

use File::Copy;

require File::Find;

binmode(STDOUT, ":encoding(utf8)");
binmode(STDIN, ":encoding(utf8)");
binmode(STDERR, ":encoding(utf8)");

my $tmp_path = '/tmp/.fastFFI_licence_tmp';

my $license_1 =
'/*
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
 */';

my $license_2 =
'# Copyright 1999-2021 Alibaba Group Holding Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.';

my $license_3 =
'<!--
 Copyright 1999-2021 Alibaba Group Holding Ltd.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->';

sub prepend_licence {
  my $name = shift;
  my $license = shift;
  my $target_line = shift;
  my $line = 1;

  open(FH, "<$name") or die "Cannot open $name$!";

  open(FH_TMP, ">$tmp_path") or die "Cannot open $tmp_path: $!";

  while(<FH>) {
    if ($_ =~ /Alibaba Group Holding Ltd/) {
      close(FH_TMP);
      close(FH);

      unlink($tmp_path);
      return;
    }

    if ($line == $target_line) {
      print FH_TMP "$license\n";
    }
    $line++;
    print FH_TMP $_;
  }

  close(FH_TMP);
  close(FH);
  unlink($name);
  move($tmp_path, $name);
}

sub callback {
  unless (-d) {
    my $dir = $File::Find::dir;
    my $path = $File::Find::name;
    my $name = $_;
    if ($dir =~ /\.git/ || $name eq '.gitignore') {
      return;
    }

    if ($name =~ /\.java\z/) {
      prepend_licence($name, $license_1, 1);
    } elsif ($name =~ /\.xml\z/) {
      # skip first line
      prepend_licence($name, $license_3, 2);
    } elsif ($name =~ /\.sh\z/) {
      # skip first line
      prepend_licence($name, $license_1, 2);
    } elsif ($name =~ /\.cc\z/ || $name =~ /\.cpp\z/ || $name =~ /\.h\z/ || $name =~ /\.hpp\z/) {
      prepend_licence($name, $license_1, 1);
    } elsif ($name eq 'CMakeLists.txt') {
      prepend_licence($name, $license_2, 1);
    } else {
      print "Skip $path\n"
    }
  };
}

my @recursiveFolder = qw(.);

File::Find::find(\&callback, @recursiveFolder);
