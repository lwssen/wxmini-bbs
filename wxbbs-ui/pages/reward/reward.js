// pages/reward/reward.js
//获取应用实例
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    wxPay: true,
    aliPay: false,
    active: 'reward',
    messageCount: ''
  },
  shoWpay(e){
     console.log(e)
     let wx=!this.data.wxPay;
     let ali=!this.data.aliPay;
     this.setData({
       wxPay: wx,
       alipay: ali,
     })
     console.log("微信 等于："+wx)
     console.log("支付宝 等于："+ali)
   },

   handlerChange(e){
    console.log("父组件事件开始"  )
    console.log(e)
    console.log("父组件事件结束"  )
    },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      messageCount: app.globalData.messageCount
    })
    app.getUserMessage();
  
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    console.log("onready")
    this.setData({
      active: 'reward'
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