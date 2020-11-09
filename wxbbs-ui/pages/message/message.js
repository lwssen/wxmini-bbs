import { request } from "../../request/index.js";

// pages/message/message.js
//获取应用实例
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    pageNum:0,
    messageList:[],
    messageType:1,
    showComment: false,
    replyContent: '',
    popupPlaceholder: '',
    postComment:{},
    active: "1",
    hasNextPage: false,
    tabBarActive: 'message',
    messages:{
      likeCount:'',
    messageCount:'',
    notificationCount:'',
    }
  },
  queryParams:{
    pageNum: 1,
    pageSize: 10
  },


  onChange(e){
    this.clearMessage();
    let myUserInfo = wx.getStorageSync('userInfos');
    let user = Object.keys(myUserInfo);
    if(user.length==0 ){
      return;
    }
   let messageType=e.detail.name;
   let queryParams=this.queryParams;
   queryParams.messageType=messageType;
   queryParams.pageNum=1;
   this.setData({
     queryParams,
     messageType,
     messageList:[]
   });
   this.getMessageList();
   this.clearMessage();
  },
//跳转帖子详情
  jumpPostInfo(e){
    let postId=e.currentTarget.dataset.post.postId;
    wx.navigateTo({
      url: '/pages/postinfo/postInfo?postId='+postId,
    })
    console.log(e)
  },
  //显示回复文本框
  showCommentField(e){
    console.log(e)
    let temp={
      postId: e.currentTarget.dataset.wxuser.postId,
      parentId: e.currentTarget.dataset.wxuser.postCommentId,
      messageType:2,
      userId: e.currentTarget.dataset.wxuser.wxUser.id,
      commentContent: ''
    }
     this.setData({
       showComment: true,
       popupPlaceholder: '回复: '+e.currentTarget.dataset.wxuser.wxUser.wxNickname,
       postComment: temp
     })
  },
  //关闭回复框
  onClose() {
    this.setData({ showComment: false });
  },
  //获取回复内容值
  contentValue(e){
    this.setData({
      replyContent: e.detail
    })
  },
  //回复消息
  pushReplyComment(e){
    let temp=this.data.postComment;
      temp.commentContent=this.data.replyContent;
    console.log("回复内容等于:"+this.data.replyContent)
    this.setData({
      postComment: temp
    }),
    request({
      url: '/post-comment/push',
      data: this.data.postComment,
      method: 'POST'
    }).then(res=>{
      const t=this;
      let code=res.data.code;
      if(code==200){
        let queryParams=t.queryParams;
     queryParams.pageNum=1;
        t.setData({
          replyContent: '',
          showComment: false,
          queryParams: queryParams
        }),
        t.getMessageList();
        wx.showToast({
          title: '回复成功',
          image: '../../myicons/success.png'
        })
      }else{
        wx.showToast({
          title: res.data.message,
          image: '../../myicons/error.png'
        })
      }
    })
  },
 //消息列表
  getMessageList(){
    request({
      url:'/user-messages',
      data: this.queryParams
    }).then(res=>{
      this.setData({
        messageList: res.data.data.list,
        hasNextPage: res.data.data.hasNextPage
        })
    })
  },
  /**
   * 清空消息
   */
  clearMessage(){
    const t=this;
   let messageType=this.data.messageType;
    request({
      url: '/user-messages/clear?messageType='+messageType,    
      method: 'DELETE'
    }).then(res=>{
        t.getMessageTypes();
    })
  },
   /**
   * 全部消息
   */
  getMessageTypes(){
    //console.log('全局消息数等于：'+app.globalData.messageCount);
    let messageCount=app.globalData.messageCount
    const t=this;
    request({
      url: '/user-messages/type/count',    
      method: 'GET'
    }).then(res=>{
      let data=res.data;
      if(data.code==200){
        t.setData({
          messages: data.data,
        //  messageCount: messageCount
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
  let queryParams = this.queryParams
   let messageTypeTemp= this.data.messageType;
   queryParams.messageType = messageTypeTemp;
   this.setData({
    queryParams
   })
   
   this.getMessageList();
   this.getMessageTypes();
   app.getUserMessage();
   
  },

  
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
    onReady: function () {
    // this.clearMessage();
    // app.refreshMessageCount()
    
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
    this.setData({
      messageList: []
    })
    this.queryParams.pageNum=1;
    this.getMessageList();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    this.queryParams.pageNum++;
    if(this.data.hasNextPage){
      request({
        url:'/user-messages',
        data: this.queryParams
      }).then(res=>{
        this.setData({
          messageList: [...this.data.messageList,...res.data.data.list],
          hasNextPage: res.data.data.hasNextPage
          })
      })
    }else{
      wx.showToast({
        title: '没有更多数据了',
        image: '../../myicons/nonedata.png'
      })
    }
    
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})