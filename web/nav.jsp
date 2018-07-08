<%--
  Created by IntelliJ IDEA.
  User: 舒意恒
  Date: 2018/7/8
  Time: 20:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<nav> <!-- 顶部栏 -->
    <div class="nav-wrapper blue row" style="margin: 0">
        <!-- Logo -->
        <div class="col s3">
            <a class="brand-logo">
                <i class="material-icons">looks</i>景区信息管理</a>
        </div>
        <!-- 搜索框 -->
        <div class="nav-wrapper col s6" style="padding: 5px">
            <form id="search_form" action="search"> <!--请求 SearchServlet 的服务-->
                <div class="input-field blue lighten-1">
                    <input id="search" type="search" name="keywords" placeholder="搜索" required>
                    <label class="label-icon" for="search"><i class="material-icons"
                                                              style="vertical-align:bottom">search</i></label>
                    <i class="material-icons" id="search-delete">close</i>
                </div>
                <input type="hidden" id="search_type" name="type" value="book">
            </form>
        </div>
        <div class="col s3">
            <a class="right" href="${pageContext.request.contextPath}/logout"> <i class="material-icons">exit_to_app</i></a>
        </div>
    </div>
</nav>
