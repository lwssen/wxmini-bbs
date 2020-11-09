// pages/login/login.js
import{ request } from "../../request/index.js"
//获取应用实例
const app = getApp()
Page({
  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },


  getUserInfo: function(e) {
    const t=this;
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
     //设置加密签名等数据缓存
    // let wxmessage={
    //   encryptedData: e.detail.encryptedData,
    //   iv: e.detail.iv,
    //   signature: e.detail.signature,
    //   rawData: e.detail.rawData
    // }
    // wx.setStorageSync('wxmessage', wxmessage)
    //设置用户信息缓存
    let session=wx.getStorageSync('session');
    let myuserInfos=e.detail.userInfo;
    myuserInfos.openId=session.openid;
    myuserInfos.sessionKey=session.sessionKey;
    myuserInfos.wxNickname=myuserInfos.nickName;
    console.log('myuserInfos等于');
    console.log(myuserInfos);
    wx.setStorageSync('userInfos',myuserInfos)
    app.globalData.isLogin=true;
    //是否执行了app.js的方法
    //app.globalData.runApp=true;
   // t.getAcessToken(myuserInfos);
    request({
      url:'/wx/user/save',
      data: myuserInfos,
      method: 'POST'
    }).then(resp=>{
      let data=resp.data;
      if(data.code==200){
        let token=data.data;
        console.log("获取到的token等于："+token);
        app.globalData.isLogin=true;
        wx.setStorageSync('token',token)
        wx.navigateBack({
         delta: 1
        })
        // wx.reLaunch({
        //   url: '/pages/home/home',
        // })
      }else{
        console.log("获取token失败,失败原因："+data.message);
        // wx.reLaunch({
        //   url: '/pages/login/login',
        // })
      }
    })
  },


  //获取token
  getAcessToken(userInfos){
    request({
      url:'/wx/user/save',
      data: userInfos,
      method: 'POST'
    }).then(resp=>{
      let data=resp.data;
      if(data.code==200){
        let token=data.data;
        console.log("获取到的token等于："+token);
        wx.setStorageSync('token',token)
        wx.reLaunch({
          url: '/pages/home/home',
        })
      }else{
        console.log("获取token失败,失败原因："+data.message);
        wx.reLaunch({
          url: '/pages/login/login',
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
     
    } else if (this.data.canIUse){
      console.log(JSON.stringify(app.globalData.userInfo))
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    } else {
      console.log(JSON.stringify(app.globalData.userInfo))
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true
          })
        }
      })
    }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})