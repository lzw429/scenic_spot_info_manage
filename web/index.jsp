<%--
  Created by IntelliJ IDEA.
  User: 舒意恒
  Date: 2018/7/3
  Time: 18:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <%@ include file="header.jsp" %>
    <title>智慧景区</title>
</head>
<body>
<!-- Navbar goes here -->
<jsp:include page="nav.jsp"/>
<!-- Page Layout here -->

<div class="row" style="margin: 0;">
    <div class="col s3" style="margin-top: 25px;">
        <div class="row">
            <div class="col s12">
                <ul class="tabs">
                    <li class="tab col s4"><a class="active" href="#test1">公告</a></li>
                    <li class="tab col s4"><a href="#test2" onclick="spotSort()">推荐</a></li>
                </ul>
            </div>
            <div id="test1" class="col s12">
                <div class="card">
                    <div class="card-image">
                        <img src="img/sample-1.jpg">
                        <span class="card-title">智慧景区欢迎您</span>
                    </div>
                    <div class="card-content">
                        <% if (application.getAttribute("announcement") == null) { %>
                        <p>7月13日 星期五</p>
                        <p>沈阳市 24℃~32℃</p>
                        <% } else { %>
                        <p>
                            <%=application.getAttribute("announcement")%>
                        </p>
                        <%} %>
                    </div>
                </div>
            </div>
            <div id="test2" class="col s12">
                <ul class="collapsible" id="spotSortList">

                </ul>

            </div>
        </div>

    </div>

    <div id="echarts" class="col s9" style="width: 1010px;height: 520px; margin-top: 25px;"><!--ECharts DOM-->
    </div>
    <div class="fixed-action-btn toolbar"> <!-- 浮动操作按钮 FAB -->
        <a class="btn-floating btn-large red">
            <i class="large material-icons">unfold_more</i>
        </a>
        <ul> <!-- 浮动操作按钮的工具栏 -->
            <li class="waves-effect waves-light"><a href="#shortestPathModal" class="modal-trigger"><i
                    class="material-icons">navigation</i>最短路线规划</a>
            </li>
            <li class="waves-effect waves-light"><a href="#tourPathModal" class="modal-trigger"><i
                    class="material-icons">map</i>导游路线图</a>
            </li>
        </ul>
    </div>

    <div id="shortestPathModal" class="modal bottom-sheet"> <!-- 最短路线规划 模态框-->
        <div class="modal-content"> <!--模态框内容-->
            <h4>最短路线规划</h4>
            <div class="input-field col s3">
                <input id="startSpotName" type="text" class="validate">
                <label for="startSpotName">起始景点</label>
            </div>
            <div class="input-field col s3">
                <input id="endSpotName" type="text" class="validate">
                <label for="endSpotName">到达景点</label>
            </div>
        </div>
        <div class="modal-footer"> <!--模态框底部-->
            <a href="#!" class="modal-close waves-effect waves-green btn-flat" id="shortestPathSubmit"
               onclick="shortestPath()">确定</a>
        </div>
    </div>

    <div id="tourPathModal" class="modal bottom-sheet"> <!-- 导游路线图 模态框-->
        <div class="modal-content"> <!--模态框内容-->
            <h4>导游路线图</h4>
            <div class="input-field col s3">
                <input id="rootSpotName" type="text" class="validate">
                <label for="rootSpotName">出发景点</label>
            </div>
        </div>
        <div class="modal-footer"> <!--模态框底部-->
            <a href="#!" class="modal-close waves-effect waves-green btn-flat" id="tourPathSubmit"
               onclick="tourPath()">确定</a>
        </div>
    </div>
</div>


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
        }
    );

    $(document).ready(function () {
        $('ul.tabs').tabs();
    });

    $('.fixed-action-btn').floatingActionButton({
        toolbarEnabled: true
    });

    $(document).ready(function () {
        $('.modal').modal();
    });

    $(document).ready(function () {
        $('.collapsible').collapsible();
    });

    function shortestPath() {
        if ($('#startSpotName').val() === $('#endSpotName').val()) {
            M.toast({html: '您输入了同一景点'});
            return;
        }
        $.post('/shortestpath', {
            startSpotName: $('#startSpotName').val(),
            endSpotName: $('#endSpotName').val()
        }, function (responseText) {
            option_shortestpath = JSON.parse(responseText);
            myChart.setOption(option_shortestpath);
            console.log(responseText);
            $('#startSpotName').val('');
            $('#endSpotName').val('');
        }).fail(function (response) {
            if (response.status === 403)
                M.toast({html: '参数错误'});
            else M.toast({html: '操作异常'});
        });
    }

    function tourPath() {
        if ($('#rootSpotName').val() === "") {
            M.toast({html: '请输入出发景点'});
            return;
        }
        $.post('/tour', {
            rootSpotName: $('#rootSpotName').val()
        }, function (responseText) {
            option_tourpath = JSON.parse(responseText);
            myChart.setOption(option_tourpath);
            console.log(responseText);
            $('#rootSpotName').val('');
        }).fail(function (response) {
            if (response.status === 403)
                M.toast({html: '参数错误'});
            else M.toast({html: '操作异常'});
        });
    }

    function spotSort() {
        $.post('/spotsort', {}, function (responseText) {
            // 渲染推荐页
            $('#spotSortList').html(responseText);
        }).fail(function (response) {
            M.toast({html: '连接异常'});
        });
    }
</script>
</body>
</html>
