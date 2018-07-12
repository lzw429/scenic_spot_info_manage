<%--
  Created by IntelliJ IDEA.
  User: 舒意恒
  Date: 2018/7/8
  Time: 18:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="zh-cmn-Hans">
<head>
    <%@ include file="header.jsp" %>
    <title>登录 - 智慧景区</title>
    <script type="text/javascript">
        function changeimg() {
            var myImg = document.getElementById("code");
            var now = new Date();
            myImg.src = "checkCode.jsp?code=" + now.getTime();
        }
    </script>
</head>
<%
    Cookie[] cookies = request.getCookies();
    if (cookies != null)
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("username")) {
                if (session.getAttribute("username") != null) {
                    response.sendRedirect("/index");
                    System.out.println("login.jsp: 自动登录成功，跳转到个人主页");
                }
                break;
            }
        }
%>
<body class="grey lighten-4">
<main>
    <div class="row">
        <div class="card white"
             style="margin: 30px auto;width:100%;max-width:550px; padding-bottom: 109px; padding-left: 16px;padding-right: 16px;">
            <div class="card-content black-text">
                <p class="card-title">登录</p>
                <br>
                <form action="${pageContext.request.contextPath}/UserServlet" method="post" id="login">
                    <input type="hidden" name="method" value="login"/>
                    <div class="row">
                        <div class="input-field s12">
                            <input id="username" name="username" type="text" class="validate">
                            <label for="username">帐号</label>
                        </div>
                    </div>
                    <div class="row">
                        <div class="input-field s12">
                            <input id="password" name="password" type="password" class="validate">
                            <label for="password">密码</label>
                        </div>
                    </div>
                    <div class="row">
                        <div class="input-field s12">
                            <input type="text" id="checkcode" name="checkcode" class="validate">
                            <label for="checkcode">验证码</label>
                        </div>
                        <!--点击验证码图片将刷新-->
                        <div>
                            <a href="javascript:changeimg()">
                                <img id="code" src="${pageContext.request.contextPath}/checkCode.jsp" type="image/jpg"></a>
                            <a href="javascript:changeimg()" style="position: relative;top:-3px;left:10px">看不清，换一张</a>
                        </div>
                    </div>
                    <br>
                    <a class="black-text">新用户？</a><a href="${pageContext.request.contextPath}/register">注册</a>
                    <button type="submit" class="waves-effect waves-light btn right blue">登 录
                    </button>
                </form>
                <script>
                    $(document).ready(function () {
                        function check_input() {
                            if (document.getElementById('username').value === "" || document.getElementById('password').value === "") {
                                M.toast({html: '请输入用户名和密码'});
                                changeimg();
                                return false;
                            }
                            var checkcode = document.getElementById('checkcode').value;
                            if (checkcode === "") {
                                M.toast({html: '请输入验证码'});
                                changeimg();
                                return false;
                            }
                            else if (checkcode.length !== 4) {
                                M.toast({html: '验证码错误'});
                                changeimg();
                                return false;
                            }
                            return true;
                        }

                        $('#login').submit(function (event) {
                            event.preventDefault();
                            var check = check_input();
                            if (check === true) {
                                $.get('${pageContext.request.contextPath}/UserServlet', {
                                    username: $('#username').val(),
                                    password: $('#password').val(),
                                    checkcode: $('#checkcode').val()
                                }, function (respondText) { // 服务器响应 "/admin"
                                    if (respondText === "/admin") {
                                        window.location.href = respondText; // 跳转 admin
                                        M.toast({html: '登录成功'});
                                    }
                                    else if (respondText === "verification failed") {
                                        M.toast({html: '验证码错误'});
                                        changeimg();
                                    }
                                }).fail(function () { // 服务器响应 403
                                    M.toast({html: '用户名或密码错误'});
                                    changeimg();
                                })
                            }
                        });
                    });
                </script>
            </div>
        </div>
    </div>
</main>
</body>
</html>