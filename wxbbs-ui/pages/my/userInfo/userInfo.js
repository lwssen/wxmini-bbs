// pages/my/userInfo/userInfo.js
import{ request } from "../../../request/index.js"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo:{
      wxNickname: '',
      address:'',
  },
  },
  //获取当前用户信息
  getUserInfo(){
    const t=this;
    request({
      url:'/my'
    }).then(res=>{
    let code=res.data.code;
      if(code==200){
        t.setData({
          userInfo:res.data.data
        });
      }else{
        wx.showToast({
          title: res.data.message,
          icon: 'none'
        })
      }
    })
  },
  changeName(e){
    let temp=this.data.userInfo;
    let name=e.detail;
    temp.wxNickname=name;
    this.setData({
      userInfo: temp
    })
  },
  changeAddress(e){
    let temp=this.data.userInfo;
    let address=e.detail;
    temp.address=address;
    this.setData({
      userInfo: temp
    })
  },
  submit(){
    let myUserInfo = wx.getStorageSync('userInfos');
    let user = Object.keys(myUserInfo);
    if(user.length==0 ){
      return;
    }
    const t=this;
    request({
      url:"/my",
      data: this.data.userInfo,
      method: 'PUT'
    }).then(res=>{
      let code=res.data.code;
      if(code==200){
        wx.showToast({
          title: '修改成功',
          image: '../../../myicons/success.png'
          })
        t.setData({
          userInfo: t.data.userInfo
        });
      }else{
        wx.showToast({
          title: res.data.message,
          image: '../../../myicons/error.png'
        })
      }
    })
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let myUserInfo = wx.getStorageSync('userInfos');
    let user = Object.keys(myUserInfo);
    if(user.length==0 ){
      return;
    }
     this.getUserInfo();
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