#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
#add you packages folder to sys.path, folder path must not ended with "\"
sys.path.append(r"C:\MySourceCode\github\MySource\python\MyPackages")

import errorLogCheck

searchKey = "<ReasonCode>130133</ReasonCode>"
fieldsList = ["requestID","Throwable","requestTime"]
fileName = r"C:\Users\JiaXie\CrashPlan\Desktop\IDMA1110-1112\prod2_130133\RequestResponseMessage.log.7"
myTable = errorLogCheck.getErrorMessageAsTable(fileName,searchKey,fieldsList)

#errorLogCheck.showErrorMessageTable(myTable)
fileName = r"C:\Users\JiaXie\CrashPlan\Desktop\IDMA1110-1112\prod2_130133\RequestResponseMessage.log.3"
myTable2 = errorLogCheck.getErrorMessageAsTable(fileName,searchKey,fieldsList)

#errorLogCheck.showErrorMessageTable(myTable)
fileName = r"C:\Users\JiaXie\CrashPlan\Desktop\IDMA1110-1112\prod2_130133\RequestResponseMessage.log.15"
myTable3 = errorLogCheck.getErrorMessageAsTable(fileName,searchKey,fieldsList)

newTable = myTable + myTable2 + myTable3
errorLogCheck.showErrorMessageTable(newTable)
#print(myTable)
