@echo off

rmdir /s /q dist
mkdir dist
copy icon-logo.png dist\icon-logo.png /Y

yarn workspaces run build --env ENABLE_LOGIN_MOBILE=false --env ENABLE_SIGNUP_MOBILE=false --env ENABLE_NAV_QUOTA=false --env ENABLE_ACCOUNT_MOBILE=false --env ENABLE_ACCOUNT_EMAIL=false

