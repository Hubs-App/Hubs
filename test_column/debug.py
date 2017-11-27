#!/usr/bin/python
# -*- coding: utf-8 -*-
import os
from subprocess import Popen, PIPE

COLUMN_ROOT_PATH = '/sdcard/HotApp/Column'


def run_cmd(cmd):
    proc = Popen(cmd, shell=True, stdout=PIPE, stderr=PIPE)
    print(bytes.decode(proc.communicate()[0]).strip())


def get_column_id():
    with open('config.lua', 'r', encoding='utf-8') as src_file:
        for line in src_file.readlines():
            pair = line.split('=')
            if pair[0] == 'UUID':
                return line.split('=')[1].strip()[1:-1]

    return None


def sync_files(column_id):
    for file_path in os.listdir('.'):
        run_cmd('adb push ' + file_path + ' ' + COLUMN_ROOT_PATH + '/' + column_id + '/')


def broadcast(column_id):
    run_cmd('adb shell "am broadcast -a cn.nekocode.hot.action.NOTIFY_COLUMN_INSTALLED -e column_id \'%s\'"' % column_id)


def main():
    column_id = get_column_id()
    sync_files(column_id)
    broadcast(column_id)


if __name__ == '__main__':
    main()
