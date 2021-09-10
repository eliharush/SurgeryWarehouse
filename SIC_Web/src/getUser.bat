@echo off
cd /d %~dp0
java -cp ".;../lib/*" il.swhm.web.config.UserGrabber
