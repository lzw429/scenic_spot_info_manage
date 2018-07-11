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
            <li class="waves-effect waves-light"><a href="#addSpotModal" class="modal-trigger"><i
                    class="material-icons">add_circle_outline</i>添加景点</a>
            </li>
            <li class="waves-effect waves-light"><a href="#addPathModal" class=" modal-trigger"><i
                    class="material-icons">add_circle_outline</i>添加路线</a>
            </li>
            <li class="waves-effect waves-light"><a href="#shortestPathModal" class="modal-trigger"><i
                    class="material-icons">navigation</i>最短路线规划</a>
            </li>
            <li class="waves-effect waves-light"><a href="#tourPathModal" class="modal-trigger"><i
                    class="material-icons">map</i>导游路线图</a>
            </li>
        </ul>
    </div>
    <div id="addSpotModal" class="modal bottom-sheet"> <!-- 添加景点 模态框-->
        <div class="modal-content"> <!--模态框内容-->
            <h4>添加景点</h4>
            <div class="input-field col s3">
                <input id="spotName" type="text" class="validate">
                <label for="spotName">名称</label>
            </div>
            <div class="input-field col s3">
                <input id="spotIntro" type="text" class="validate">
                <label for="spotIntro">简介</label>
            </div>
        </div>
        <div class="modal-footer"> <!--模态框底部-->
            <a href="#!" class="modal-close waves-effect waves-green btn-flat" id="addSpotSubmit"
               onclick="addSpot()">确定</a>
        </div>
    </div>

    <div id="addPathModal" class="modal bottom-sheet"> <!-- 添加路线 模态框-->
        <div class="modal-content"> <!--模态框内容-->
            <h4>添加路线</h4>
            <div class="input-field col s3">
                <input id="spot1name" type="text" class="validate">
                <label for="spot1name">景点1名称</label>
            </div>
            <div class="input-field col s3">
                <input id="spot2name" type="text" class="validate">
                <label for="spot2name">景点2名称</label>
            </div>
            <div class="input-field col s3">
                <input id="distance" type="text" class="validate">
                <label for="distance">距离</label>
            </div>
        </div>
        <div class="modal-footer"> <!--模态框底部-->
            <a href="#!" class="modal-close waves-effect waves-green btn-flat" id="addPathSubmit"
               onclick="addPath()">确定</a>
        </div>
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

        $('.fixed-action-btn').floatingActionButton({
            toolbarEnabled: true
        });

        $(document).ready(function () {
            $('.modal').modal();
        });

        function addSpot() {
            $.post('/addspot', {
                spotName: $('#spotName').val(),
                spotIntro: $('#spotIntro').val(),
            }, function () {
                M.toast({html: '添加成功'});
                requestGraph();
                $('#spotName').val('');
                $('#spotIntro').val('');
            }).fail(function () {
                M.toast({html: '操作异常'});
            })
        }

        function addPath() {
            $.post('/addpath', {
                spot1name: $('#spot1name').val(),
                spot2name: $('#spot2name').val(),
                distance: $('#distance').val()
            }, function () {
                M.toast({html: '添加成功'});
                requestGraph();
                $('#spot1name').val('');
                $('#spot2name').val('');
                $('#distance').val('');
            }).fail(function () {
                M.toast({html: '操作异常'});
            })
        }

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
    </script>
</div>
</body>
</html>
