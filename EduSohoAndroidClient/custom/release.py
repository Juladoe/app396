# -*- coding: utf-8 -*-  
import subprocess, os, re

buildList = []

def gradleBuild(name):
	print "开始build " + name
	subp=subprocess.Popen('gradle assembleRelease', shell=True, stdout=subprocess.PIPE)
	while subp.poll()==None:
	    print subp.stdout.readline()
	print subp.returncode

def replaceBuild(build):
	tempFile = open("temp")
	buildFile = open("build.gradle", "w")
	print "开始生成release文件 " + build[0]

	for line in tempFile.readlines():
		it = re.finditer("({.+})", line)
		for match in it:
			group = match.group(1)
			span = match.span()

			if group == "{package}":
				line = line.replace(group, build[0])
			elif group == "{storePassword}":
				line = line.replace(group, build[1][0])
			elif group == "{keyPassword}":
				line = line.replace(group, build[1][1])

		buildFile.write(line)
	buildFile.close()

	print "生成release文件 " + build[0]

def readPass(name):
	passList = []
	passFile = open("key/" + name + ".pass")
	for line in passFile.readlines():
		if line.find("\n") != -1:
			line = line[:-1]
		passList.append(line)
	return passList

def readKeys():
	dirList = os.listdir("key")
	print "解析key文件"

	for file in dirList:
		ext = os.path.splitext(file)
		if ext[1] == ".jks":
			build = []
			build.append(ext[0])
			buildList.append(build)
		elif ext[1] == ".pass":
			build = buildList[-1]
			build.append(readPass(ext[0]))


def run():
	readKeys()
	for build in buildList:
		replaceBuild(build)
		gradleBuild(build[0])

run()