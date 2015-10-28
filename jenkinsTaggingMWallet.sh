#!/bin/bash

PREVTAG=$1
PREVTAG=${PREVTAG:4:3}
TAG=$((PREVTAG+1))
APP_VERSION_INTERNAL='1.1.'$TAG
svn copy --username lewisc --password mobilisaccessonly http://customer.mobilis.com:10000/svn/AndroidWallet/develop/develop  http://customer.mobilis.com:10000/svn/AndroidWallet/tags/$APP_VERSION_INTERNAL  -m "#MOBAWT-1 Tagging $APP_VERSION_INTERNAL"
