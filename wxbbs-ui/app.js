//app.js
import { request } from "./request/index.js";
App({
  onLaunch: function () {
    // 展示本地存储能力
    let appid='wx77f2306860352fed';
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
    //获取未读消息
    // request({
    //   url: '/user-messages/count'
    // }).then(res=>{
    //      let messageCount=res.data.data; 
    //      messageCount=messageCount > 0 ?messageCount: '';
    //      // console.log("消息条数等于："+messageCount)
    //      this.globalData.messageCount = messageCount;
    // })
    var myUserInfo = wx.getStorageSync('userInfos');
    let isLogin=this.globalData.isLogin;
    if(myUserInfo !=null && myUserInfo !='' && !isLogin){
       console.log("appJs===已存在用户openid,重新获取token")
      this.getAcessToken(myUserInfo);
      this.globalData.isLogin=true;
      this.getUserMessage();
    }
    // 登录
    wx.login({   
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
        let code=res.code
        let params={
          appid: appid,
          code: code
        }
        request({
          url: '/wx/user/'+appid+'/login',
          data: params
        }).then(resp=>{
          let data=resp.data;
          if(data.code==200){
           let temp= data.data;
           let session=JSON.parse(temp);
           // console.log("session等于");
           // console.log(session)
           wx.setStorageSync('session', session)
      
          }
        })
      }
    })
    // 获取用户信息
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          // console.log("appjs")
          // console.log(res)
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: res => {
              // 可以将 res 发送给后台解码出 unionId
             
              this.globalData.userInfo = res.userInfo

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }else{
          wx.showModal({
            title: '提示',
            content: '当前处于未登录状态，有可能影响使用,是否前往登录页进行登录？',
            cancelText: "取消登录",
            cancelColor: "#FE2E2E",
            confirmText: "前往登录",
            confirmColor: "#07c160",
            success: (res) => {
              if (res.confirm) {
                // console.log('用户点击确定')
                wx.navigateTo({
                  url: '/pages/login/login',
                })
              } else if (res.cancel) {
                // console.log('用户点击取消')
                // wx.navigateTo({
                //   url: '/pages/login/login',
                // })
              }
              
            },
          })
        }
      }
    })
  },

   //获取token
   getAcessToken(userInfos){
    request({
      url:'/wx/user/get/token',
      data: userInfos,
      method: 'POST'
    }).then(resp=>{
      let data=resp.data;
      if(data.code==200){
        let token=data.data;
        // console.log("获取到的token等于："+token);
        wx.setStorageSync('token',token)
      }else{
        // console.log("获取token失败,失败原因："+data.message);
      }
    })
  },
  globalData: {
    userInfo: null,
    messageCount:'',
    messages: {},
//    baseUrl: 'http://192.168.0.102:9080/backapi',
   baseUrl: 'https://www.sssen.top/backapi',
    isLogin: false,
    anonymousUserId: null
  },
  //生成匿名用户ID
  generateAnonymousUserId(){
    let str = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    let result = '';
    for (var i = 26; i > 0; --i) {
      result += str[Math.floor(Math.random() * str.length)];
    }
    var str2 = ['0','1','2','3','4','5','6','7','8','9'];
    var res = "";
    for(var i = 0; i < 6 ; i ++) {
      var id = Math.floor(Math.random() * str2.length);
      res += str2[id];
    }
     let randomUserId=res+result;
     this.globalData.anonymousUserId=randomUserId;
  },
  //检查用户是否已经登录
  checkLogin(){
    var myUserInfo = wx.getStorageSync('userInfos');
    var user = Object.keys(myUserInfo);
    if(user.length==0 ){
      wx.navigateTo({
        url: '/pages/login/login',
      })
      return;
    }
      
      // wx.showModal({
      //   title: '提示',
      //   content: '当前处于未登录状态，不允许此操作,是否前往登录页进行登录？',
      //   cancelText: "取消登录",
      //   cancelColor: "#FE2E2E",
      //   confirmText: "前往登录",
      //   confirmColor: "#07c160",
      //   success: (res) => {
      //     if (res.confirm) {
      //       wx.navigateTo({
      //         url: '/pages/login/login',
      //       })
      //       return;
      //     } else if (res.cancel) {
           
      //     }
          
      //   },
      // })
  //  }
  },
  

  //获得用户消息
  getUserMessage(){
    const t=this;
    request({
      url: '/user-messages/count'
    }).then(res=>{
         let messageCount=res.data.data; 
         messageCount=messageCount > 0 ?messageCount: '';
         // console.log("消息条数等于："+messageCount)
         this.globalData.messageCount=messageCount
       
    })
    
  },

  //刷新用户消息
  refreshMessageCount(){
    const t=this;
    request({
      url: '/user-messages/type/count'
    }).then(res=>{
         let tempMessage=res.data.data;
      this.globalData.messages=tempMessage
      this.globalData.messageCount=tempMessage.messageCount   
      // console.log("刷新后得到的消息条数："+this.globalData.messageCount)
    })
  }
})