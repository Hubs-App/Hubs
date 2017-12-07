#!/usr/bin/python
# -*- coding: utf-8 -*-
from subprocess import Popen, PIPE
import codecs

APPLICATION_ID = 'cn.nekocode.hubs'
HUB_ROOT_PATH = 'Hub'
TMP_PATH = '/data/local/tmp'


def run_cmd(cmd):
    proc = Popen(cmd, shell=True, stdout=PIPE, stderr=PIPE)
    print(bytes.decode(proc.communicate()[0]).strip())


def get_hub_id():
    with codecs.open('config.lua', 'r', encoding='utf-8') as src_file:
        for line in src_file.readlines():
            pair = line.split('=')
            if pair[0] == 'ID':
                return line.split('=')[1].strip()[1:-1]

    return None


def sync_files(hub_id):
    tmp_hub_path = TMP_PATH + '/' + hub_id
    run_cmd('adb push . %s/' % tmp_hub_path)
    run_cmd('adb shell "run-as %s cp -r %s %s/"' % (APPLICATION_ID, tmp_hub_path, HUB_ROOT_PATH))


def broadcast(hub_id):
    run_cmd('adb shell "am broadcast -a cn.nekocode.hubs.action.NOTIFY_HUB_INSTALLED -e hub_id \'%s\'"' % hub_id)


def main():
    hub_id = get_hub_id()
    sync_files(hub_id)
    broadcast(hub_id)


if __name__ == '__main__':
    main()
