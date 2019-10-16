#!/bin/bash
echo 开始删除文件


function deleteIdea(){
    filePath=$1$2
    #echo "filePath: ${filePath}"
    if [[ -e $filePath ]]; 
    then
       echo "delete ${filePath}"
       rm -rf $filePath
    fi
}

function getdir(){
    for file in `ls -a $1`; 
    do
        path=$1"/"$file
        #echo "path: ${path}"
        if [[ -d $path ]]; 
        then
            if [[ $file = "." || $file = ".." ]]; #去掉 ..目录
            then
                # echo "ignore ${path}"
                continue
            elif [[ $file = ".git" ]]; #忽略 git 目录
            then
                echo "ignore ${path}"
            elif [[ $file = "build" ]]; #如果是 build 目录删除
            then
               echo "delete ${path}"
               rm -rf $path
            elif [[ $file = ".gradle" ]]; #如果是.gradle 删除目录
            then
                echo "delete ${path}"
                rm -rf $path
            elif [[ $file = ".idea" ]]; #如果是.idea 删除目录下的不必要的文件
            then
                #echo "delete ${path} 下的不必要的文件"
                deleteIdea $path "/caches"
                deleteIdea $path "/libraries"
                deleteIdea $path "/modules.xml"
                deleteIdea $path "/workspace.xml"
                deleteIdea $path "/navEditor.xml"
                deleteIdea $path "/assetWizardSettings.xml"
                deleteIdea $path "/encodings.xml"  

                getdir $path     
            elif [[ $file = "captures" ]]; #如果是captures 删除目录
            then
                echo "delete ${path}"
                rm -rf $path
            elif [[ $file = ".externalNativeBuild" ]]; #如果是.externalNativeBuild 删除目录
            then
                echo "delete ${path}"
                rm -rf $path
            else
                getdir $path
            fi
        else #如果是文件类型
            #echo "file: ${path}"
            ext=${file##*.} #得到文件的后缀
            #echo "ext: ${ext}"
            if [[ $file = ".DS_Store" ]]; #删除.DS_Store文件
            then
                echo "delete ${path}"
                rm -rf $path
            elif [[ $file = "local.properties" ]]; 
            then
                echo "delete ${path}"
                rm -rf $path
            elif [[ $ext = "iml" ]]; #删除以.iml 结尾的文件
            then
                echo "delete ${path}"
                rm -rf $path
            fi
        fi
    done
}

rootDir=`pwd`$1
echo "rootDir: ${rootDir}"
getdir $rootDir


# rm -rf output *.zip *.apk *.iml 
echo 删除文件结束
