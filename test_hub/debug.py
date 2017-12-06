#!/usr/bin/python
# -*- coding: utf-8 -*-
import os
from subprocess import Popen, PIPE

HUB_ROOT_PATH = '/sdcard/HubsApp/Hub'


def run_cmd(cmd):
    proc = Popen(cmd, shell=True, stdout=PIPE, stderr=PIPE)
    print(bytes.decode(proc.communicate()[0]).strip())


def get_hub_id():
    with open('config.lua', 'r', encoding='utf-8') as src_file:
        for line in src_file.readlines():
            pair = line.split('=')
            if pair[0] == 'ID':
                return line.split('=')[1].strip()[1:-1]

    return None


def sync_files(hub_id):
    run_cmd('adb push . ' + HUB_ROOT_PATH + '/' + hub_id + '/')


def broadcast(hub_id):
    run_cmd('adb shell "am broadcast -a cn.nekocode.hubs.action.NOTIFY_HUB_INSTALLED -e hub_id \'%s\'"' % hub_id)


def main():
    hub_id = get_hub_id()
    sync_files(hub_id)
    broadcast(hub_id)


if __name__ == '__main__':
    main()
