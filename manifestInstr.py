#!/usr/bin/python

import xml.etree.ElementTree as ET
import sys

def insertReadWritePerms(file):
	tree = ET.parse(file)
	root = tree.getroot()
	#root == manifest
	read = ET.SubElement(root, 'uses-permission')
	read.set('android:name', 'android.permission.READ_EXTERNAL_STORAGE')
	write = ET.SubElement(root, 'uses-permission')
	write.set('android:name', 'android.permission.WRITE_EXTERNAL_STORAGE')
	tree.write(file)


def main(argv):
	for arg in argv:
		insertReadWritePerms(arg)

if __name__ == "__main__":
   main(sys.argv[1:])