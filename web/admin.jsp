<%--
  Created by IntelliJ IDEA.
  User: 舒意恒
  Date: 2018/7/6
  Time: 20:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <%@ include file="header.jsp" %>
    <title>景区信息管理系统</title>
</head>
<body>
<!-- Navbar goes here -->
<jsp:include page="nav.jsp"/>
<!-- Page Layout here -->
<div class="row">
    <div id="echarts" style="width: 1200px;height: 520px; margin-top: 25px;"><!--ECharts DOM-->

    </div>
    <div class="fixed-action-btn toolbar"> <!-- 浮动操作按钮 FAB -->
        <a class="btn-floating btn-large red">
            <i class="large material-icons">unfold_more</i>
        </a>
        <ul> <!-- 浮动操作按钮的工具栏 -->
            <li class="waves-effect waves-light"><a href="#!"><i class="material-icons">insert_chart</i></a></li>
            <li class="waves-effect waves-light"><a href="#!"><i class="material-icons">format_quote</i></a></li>
            <li class="waves-effect waves-light"><a href="#!"><i class="material-icons">publish</i></a></li>
            <li class="waves-effect waves-light"><a href="#!"><i class="material-icons">attach_file</i></a></li>
        </ul>
    </div>
    <script type="text/javascript">
        document.addEventListener('DOMContentLoaded', function () {
            var elems = document.querySelectorAll('.fixed-action-btn');
            var instances = M.FloatingActionButton.init(elems, {
                toolbarEnabled: true
            });
        });
    </script>
    <script type="text/javascript">
        myChart = {};
        option = {};

        function requestGraph() {
            $.ajax({
                url: '${pageContext.request.contextPath}/graph',
                type: 'POST',
                dataType: "text",
                success: function (responseText) {
                    option = JSON.parse(responseText);
                    myChart.setOption(option);
                    console.log(responseText);
                }
            }).fail(function () {
                M.toast({html: '连接异常'});
            });
        }

        $(document).ready(function () {
            myChart = echarts.init(document.getElementById('echarts'));
            requestGraph();
        });
    </script>
</div>
</body>
</html>
