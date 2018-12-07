//js模块化,json格式
const seckill = {

    /**
     * 获取秒杀暴露地址,执行秒杀
     */
    handlerSeckill(seckillId, node) {
        //先隐藏秒杀按钮
        node.hide().html('<button id="killBtn" class="btn btn-primary btn-lg">抢购</button>');
        //请求秒杀暴露地址
        $.post(seckill.URL.exposer(seckillId), {}, function (res) {
            if (res.success) {
                let exposer = res.data;
                //已暴露地址(已开启秒杀)
                if (exposer.exposed) {
                    //获取暴露的秒杀地址
                    let killUrl = seckill.URL.execution(seckillId, exposer.md5);
                    console.log('暴露的秒杀地址=' + killUrl);
                    //为生成的秒杀按钮只绑定一次点击事件,点击一次后失效
                    $('#killBtn').one('click', function () {
                        //先禁用按钮
                        $(this).addClass('disabled');
                        //请求服务器执行秒杀
                        $.post(killUrl, {}, function (res) {
                            //秒杀成功
                            if (res && res.success) {
                                let killResult = res.data;
                                let state = killResult.state;
                                let stateInfo = killResult.stateInfo;
                                //秒杀结果放入节点
                                node.html('<span class="label label-success">' + stateInfo + '</span>');
                            } else {
                                console.error('errorMsg=' + JSON.stringify(res));
                            }
                        });
                    });
                    //请求完数据后显示节点信息
                    node.show();
                } else {  //未开始秒杀
                    //获取秒杀开始时间、当前时间、结束时间
                    let now = exposer.now;
                    let start = exposer.start;
                    let end = exposer.end;
                    //重新计时
                    seckill.countDown(seckillId, now, start, end);
                }
            } else {
                console.error('errorMsg=' + JSON.stringify(res));
            }
        });
    },

    //验证手机号是否合法
    validatePhone(phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) { //isNaN()是否是非数字值
            return true;
        } else {
            return false;
        }
    },

    //倒计时
    countDown(seckillId, nowTime, startTime, endTime) {
        //计时页面组件
        let seckillBox = $("#seckill-box");
        console.log('nowTime=' + nowTime + ',startTime=' + startTime + ',endTime=' + endTime);
        //判断是否开始计时
        if (nowTime > endTime) {
            console.log('秒杀结束');
            //秒杀结束
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            //秒杀未开始
            console.log('秒杀未开始');
            let killTime = new Date(startTime + 1000); //加1s防止客户端时间偏移
            //使用jquery-countdown工具类开始离killTime的倒计时
            seckillBox.countdown(killTime, function (event) { //每计数1次执行一次
                //格式化显示
                let formatStr = event.strftime('离秒杀开始还有：%D天 %H小时 %M分钟 %S秒');
                seckillBox.html(formatStr);
            }).on('finish.countdown', function () {  //倒计时完成后回调函数
                //获取秒杀暴露地址,执行秒杀
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        } else {  //秒杀进行中
            console.log('秒杀进行中');
            //获取秒杀暴露地址,执行秒杀
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    },

    //封装秒杀相关ajax的url
    URL: {
        //请求系统当前时间的url
        now() {
            return '/seckill/time/now';
        },
        exposer(seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        execution(seckillId, md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },

    //详细页秒杀逻辑
    detail: {

        //详细页初始化方法
        init(params) {
            //使用jquery-cookie工具类,在cookie中查找手机号
            let killPhone = $.cookie('killPhone');
            //cookie中查找不到killPhone或不合法时执行
            if (!seckill.validatePhone(killPhone)) {
                //配置模态框
                let killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,//显示弹出层
                    backdrop: 'static',//禁止位置关闭
                    keyboard: false//关闭键盘事件
                });
                //提交按钮点击事件
                $('#killPhoneBtn').click(function () {
                    let inputPhone = $('#killPhoneKey').val();
                    console.log(inputPhone);
                    //若输入的手机号合法
                    if (seckill.validatePhone(inputPhone)) {
                        //写入cookie,7天有效期,只在路径http://localhost:8080/seckill...下有效
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面,重新开始验证逻辑
                        window.location.reload();
                    } else {
                        //先隐藏再显示错误提示
                        $('#killPhoneMessage')
                            .hide()
                            .html('<lable class="label label-danger">手机号错误!</lable>')
                            .show(300);
                    }
                })
            }

            //=================cookie中有killPhone且合法=====================
            //计时交互
            const seckillId = params['seckillId'];
            const startTime = params['startTime'];
            const endTime = params['endTime'];
            //请求服务器获取当前系统时间
            $.get(
                seckill.URL.now(),
                {},
                function (res) {
                    if (res && res.success) {
                        let nowTime = res.data;
                        //开始计时
                        seckill.countDown(seckillId, nowTime, startTime, endTime);
                    } else {
                        console.log('res=' + res);
                    }
                })

        }


    }


}