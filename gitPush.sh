#!/bin/bash
COMMENT="study something"
if [ $# -eq 1 ];then
        COMMENT=$1
fi
echo -e "\033[1;32m----- pull request -----\033[0m"
git pull
echo ""

echo -e "\033[1;32m----- add all of the modify file -----\033[0m"
git add -A
echo ""

echo -e "\033[1;32m----- commit to local repository -----\033[0m"
git commit -am "${COMMENT}"
echo ""

echo -e "\033[1;32m----- commit to gitee remote repository -----\033[0m"
git push
echo ""

echo -e "\033[1;32m----- commit to github remote repository -----\033[0m"
git push github master
echo ""

echo -e "\033[1;32m----- check status -----\033[0m"
git status