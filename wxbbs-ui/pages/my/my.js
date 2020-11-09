// pages/my/my.js
import{ request } from "../../request/index.js"
//获取应用实例
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo:null,
    isAdmin: false,
    active: 'my',
    messageCount: '',
    isPush: false
  },
  userInfo(e){
    wx.navigateTo({
      url: '/pages/my/userInfo/userInfo',
    })
  },
  
  myPosts(e){
    wx.navigateTo({
      url: '/pages/my/mypost/mypost',
    })
  },
  allPosts(e){
    wx.navigateTo({
      url: '/pages/admin/allposts/allposts',
    })
  },
  allUsers(e){
    wx.navigateTo({
      url: '/pages/admin/allusers/allUsers',
    })
  },
  allPostType(e){
    wx.navigateTo({
      url: '/pages/admin/allpostype/allPostType',
    })
  },
  allSenSitiveWord(e){
    wx.navigateTo({
      url: '/pages/admin/allsensitiveword/allSenSitiveWord',
    })
  },
  isadd(e){
    wx.navigateTo({
      url: '/pages/editor/editor',
    })
  },
  //获取当前用户信息
  getUserInfo(){
    const t=this;
    request({
      url:'/my'
    }).then(res=>{
    let code=res.data.code;
      if(code==200){
        let temp=res.data.data.isPush;
        t.setData({
          userInfo:res.data.data,
          isPush: temp
        });
      }else{
        wx.showToast({
          title: res.data.message,
          image: '../../../myicons/error.png'
        })
        setTimeout(() => {
             wx.hideToast({
             complete: (res) => {},
              })
        }, 1500);
      }
    })
  },
  checkIsAdmin(){
    const t=this;
    request({
      url:'/my/check/admin'
    }).then(res=>{
      let code=res.data.code;
      if(code==200){
        t.setData({
          isAdmin:res.data.data.isAdmin
        });
      }else{
        wx.showToast({
          title: res.data.message,
          image: '../../../myicons/error.png'
        })
        setTimeout(() => {
             wx.hideToast({
             complete: (res) => {},
              })
        }, 1500);
      }
    })
  },
  toLogin(e){
      const t=this;
      app.globalData.userInfo = e.detail.userInfo
      app.globalData.hasUserInfo=true;
      this.setData({
        userInfo: e.detail.userInfo,
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
          wx.reLaunch({
            url: '/pages/my/my',
          })
        }else{
          console.log("获取token失败,失败原因："+data.message);
          // wx.reLaunch({
          //   url: '/pages/login/login',
          // })
        }
      })
   
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var myUserInfo = wx.getStorageSync('userInfos');
    var user = Object.keys(myUserInfo);
    console.log("用户信息等于")
    console.log(user)
    if(user.length==0 ){
      // wx.reLaunch({
      //   url: '/pages/login/login',
      // })
      console.log("用户信息为空")
      return;
    }
    app.getUserMessage();
    this.setData({
      messageCount: app.globalData.messageCount
    })
      this.getUserInfo();
      this.checkIsAdmin();
  },
  
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    console.log("执行了onReady方法")
    let myUserInfo = wx.getStorageSync('userInfos');
    let user = Object.keys(myUserInfo);
    if(user.length==0 ){
      return;
    }
    app.getUserMessage();
    this.setData({
      messageCount: app.globalData.messageCount
    })
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