import { request } from "../../request/index.js";
import Toast from '../../miniprogram_npm/@vant/weapp/toast/toast';

// pages/postinfo/postInfo.js
const app=getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
    commentContentParent: '',
    postId: null,
    postComment: {},
    popupCommentContent: '',
    details: null,
    manyCommentBottomContent: {},
    manyCommentBottomPlaceholder: '',
    publishUserId: '',
    commentUserId: '',
    postContent: null,
    skeloading: true,
    postCommentList: [],
    loading: false,
    finished: false,
    likeFlag: false,
    likeCount: '',
    showPopup: false,
    showChildContent: false,
    popupPlaceholder: '',
    childCommentList: [],
    showChildPopup: false,
    childPopupCommentContent: '',
    container: null,
    masterReplyOthers: {},
    childReplyOthers: {},
    myShowLoading: true,
    currentManyPostCommentId: '',
    loadingManyComment: false,
    finishedManyComment: false,
    hasNextPage: false,
    inputValue: '',
    childManyCommentId: null
  },
  queryParams:{
    pageNum: 1,
    pageSize: 10
    
  },
  
//获取评论列表
  getComments(postId){
    const t=this;
    console.log("postId等于："+postId)
      request({
        url: '/post-comment/list/'+postId,
        data: this.queryParams
      }).then(resp=>{
        let data=resp.data;
        if(data.code==200){
            t.setData({
              postCommentList: data.data.list,
              hasNextPage: data.data.hasNextPage,
              postId: postId
            })
        }else{

        }
      })
  },
  onClose() {
    this.setData({ 
      showPopup: false,
      showChildContent: false,
     });
  },
  onClose2() {
    this.setData({ 
     showChildPopup: false,
     });
  },
  getChangeValue(e){
    let newValue=e.detail;
   // console.log("评论内容等于："+newValue)
    this.setData({
      inputValue: newValue
    })
   },

//对帖子发布评论
pushComment(e){
  //  console.log("对帖子发布评论")
  //  console.log(e)
 // app.checkLogin();
 let myUserInfo = wx.getStorageSync('userInfos');
    let user = Object.keys(myUserInfo);
    if(user.length==0 ){
      wx.navigateTo({
        url: '/pages/login/login',
      })
      return;
    }
   const t=this;
   let pushUser=this.data.details;
   console.log(pushUser)
   let postComment = {
    postId: pushUser.id,
    commentContent: t.data.inputValue,
    publishUserId: pushUser.wxUser.id
    }
    
    request({
      url: '/post-comment/push',
      data: postComment,
      method: 'POST'
    }).then(resp=>{
      let data=resp.data;
      if(data.code==200){
        Toast.success('评论成功');
        t.queryParams.pageNum=1;
        t.getComments(pushUser.id);
        t.setData({
          inputValue: '',
          commentContentParent:''
        })
        wx.showToast({
          title: '评论成功',
          image: '/myicons/success.png',
          duration: 2500
        })
        setTimeout(() => {
          wx.hideToast({
            complete: (res) => {},
          })
        }, 1500);
        
         
      }else{
        wx.showToast({
          title: data.message,
          image: '/myicons/error.png',
          duration: 2500
        })
        setTimeout(() => {
          wx.hideToast({
            complete: (res) => {},
          })
        }, 1500);
      }
    })
},

 //点击底部评论弹出回复他人文本框
 popupComment(e) {
  let index=e.currentTarget.dataset.index;
  let dataTemp=this.data.postCommentList[index];
 let nickName=dataTemp.wxUser.wxNickname;
  let temp="回复:" + nickName;
 let userId=dataTemp.wxUser.id
    console.log(dataTemp)
  this.setData({
    showPopup: true,
    popupPlaceholder: temp,
    masterReplyOthers: dataTemp,
    commentUserId: userId
  })
 
},
 //发布点击底部评论弹出回复他人文本框的内容
 replyBottomComment(e) {
  let myUserInfo = wx.getStorageSync('userInfos');
  let user = Object.keys(myUserInfo);
  if(user.length==0 ){
    wx.navigateTo({
      url: '/pages/login/login',
    })
    return;
  }
  const t = this;
  let data = this.data.masterReplyOthers;
    console.log(data);
    let postId=data.postId
  const popunComment = {
      postId: data.postId,
      parentId: data.id,
      commentContent: this.data.inputValue,
      messageType: 2,
      userId: data.userId
  };
  request({
    url: '/post-comment/push',
    data: popunComment,
    method: 'POST'
  }).then(resp=>{
    let data=resp.data;
    if(data.code==200){
      t.queryParams.pageNum=1;
      t.getComments(postId);

      wx.showToast({
        title: '回复成功',
        image: '/myicons/success.png',
        duration: 2500
      })
      t.setData({
        inputValue: '',
        popupCommentContent:'',
        showPopup: false
      })
    }else{
      wx.showToast({
        title: '回复失败',
        image: '/myicons/error.png',
        duration: 2500
      })
      setTimeout(() => {
        wx.hideToast({
          complete: (res) => {},
        })
      }, 1500);
    }
  })
 },
 //显示更多回复
 getChildManyComment(params){
  const t=this;
  request({
    url:'/post-comment/show/many/comment',
    data: params
  }).then(resp=>{
    let result=resp.data;
    if(result.code==200){
       t.setData({
         childCommentList: result.data,
         inputValue: ''
       })
    }
  })
 },
 
 showChildManyComment(e){
   const t=this;
   let index=e.currentTarget.dataset.index;
   let data=this.data.postCommentList[index];
   let value=this.data.inputValue;
   console.log(data)
   let nickName=data.wxUser.wxNickname;
   let inputMessage= "参与讨论 //@" + nickName + " 发布的评论";
   let tempQueryParams={
     id: data.id,
    postId: data.postId,
  }
   let manyCommentBottomContent = {
    postId: data.postId,
    parentId: data.id,
    commentContent: value,
    messageType: 2,
    userId: data.wxUser.id
};
t.getChildManyComment(tempQueryParams);
   this.setData({
    showChildContent: true,
    manyCommentBottomContent: manyCommentBottomContent,
    manyCommentBottomPlaceholder: inputMessage,
    childManyCommentId: data.id
   })
   
 },
 //更多回复页面底部的输入框
 replyBottomCommentInManyComment(e){
  let myUserInfo = wx.getStorageSync('userInfos');
  let user = Object.keys(myUserInfo);
  if(user.length==0 ){
    wx.navigateTo({
      url: '/pages/login/login',
    })
    return;
  }
  const t=this;
  let value=this.data.inputValue;
  let tempValue=this.data.manyCommentBottomContent;
  tempValue.commentContent=value;
   let postId=tempValue.postId;
   let id=tempValue.parentId;
   let tempQueryParams={
    id: id,
   postId: postId,
 }
  request({
    url:'/post-comment/push',
    data: tempValue,
    method: 'POST'
  }).then(resp=>{
    let result=resp.data;
    if(result.code==200){
       t.setData({
         inputValue: '',
         popupCommentContent: ''
       })
       t.getChildManyComment(tempQueryParams)
       t.queryParams.pageNum=1;
        t.getComments(postId);
       wx.showToast({
        title: '评论成功',
        image: '/myicons/success.png',
        duration: 2500
      })
      setTimeout(() => {
        wx.hideToast({
          complete: (res) => {},
        })
      }, 1500);
    }else{
      wx.showToast({
        title: '操作失败',
        image: '/myicons/error.png',
        duration: 2500
      })
      setTimeout(() => {
        wx.hideToast({
          complete: (res) => {},
        })
      }, 1500);   
    }
  })
 },
 //点击更多回复页面下弹出的内容输入框
 childPopupComment(e){
   let index=e.currentTarget.dataset.index;
   let temp=this.data.childCommentList[index];
   this.setData({
    showChildPopup: true,
    popupPlaceholder: "回复:" + temp.wxUser.wxNickname,
    commentUserId: temp.wxUser.id,
    childReplyOthers: temp
   })
 //  console.log(temp)
 },
 //发布更多回复页面下的评论内容
 pushChildPopunComment(e){
  let myUserInfo = wx.getStorageSync('userInfos');
  let user = Object.keys(myUserInfo);
  if(user.length==0 ){
    wx.navigateTo({
      url: '/pages/login/login',
    })
    return;
  }
   const t=this;
   let data=this.data.childReplyOthers;
   let value=this.data.inputValue;
  let childPopunComment = {
    postId: data.postId,
    parentId: data.id,
    commentContent: value,
    messageType: 2,
    userId: data.wxUser.id
};
let tempQueryParams={
  id: t.data.childManyCommentId,
 postId: data.postId,
}
  request({
    url: '/post-comment/push',
    data: childPopunComment,
    method: 'POST'
  }).then(resp=>{
    let result=resp.data;
    if(result.code==200){
      t.setData({
        inputValue: '',
        childPopupCommentContent: '',
        showChildPopup: false
      })
      t.getChildManyComment(tempQueryParams)
      t.queryParams.pageNum=1;
        t.getComments(data.postId);
      wx.showToast({
       title: '评论成功',
       image: '/myicons/success.png',
       duration: 2500
     })
     setTimeout(() => {
      wx.hideToast({
        complete: (res) => {},
      })
    }, 1500);
   }else{
     wx.showToast({
       title: result.message,
       image: '/myicons/error.png',
       duration: 2500
     })
     setTimeout(() => {
      wx.hideToast({
        complete: (res) => {},
      })
    }, 1500);
   }
  })
 },
 //点赞帖子
 like(){
  let token = wx.getStorageSync('token');
  if(token==''){
    let myUserInfo = wx.getStorageSync('userInfos');
    let user = Object.keys(myUserInfo);
    if(user.length==0 ){
      wx.navigateTo({
        url: '/pages/login/login',
      })
      return;
    }
  }
   const t=this;
  let userInfo=this.data.details;
  let paramsTemp={
    postId: userInfo.id,
    likeCount: userInfo.likeCount,
    publishUserId: userInfo.wxUser.id,
    getMessageUserId: userInfo.wxUser.id,
  }
  wx.request({
    url: app.globalData.baseUrl+'/posts/like',
    data:paramsTemp,
    method: 'PUT',
    header: {
      'content-type': 'application/json',
      "Author-Token": token
    },
    success (resp) {
      let result=resp.data;
      if(result.code==200){
        userInfo.isLike=result.data.isLike;
        userInfo.likeCount=result.data.likeCount;
        t.setData({
          details: userInfo
        })
     }else{
       wx.showToast({
         title: '操作失败',
         image: '/myicons/error.png',
         duration: 2500
       })
       setTimeout(() => {
        wx.hideToast({
          complete: (res) => {},
        })
      }, 1500);
     }
    }
  })
 },
 //点赞帖子底部的评论
 likeComment(e){
  let token = wx.getStorageSync('token');
  if(token==''){
    let myUserInfo = wx.getStorageSync('userInfos');
    let user = Object.keys(myUserInfo);
    if(user.length==0 ){
      wx.navigateTo({
        url: '/pages/login/login',
      })
      return;
    }
  }
  const t=this;
  let postCommentList=this.data.postCommentList;
  let index=e.currentTarget.dataset.index;
  let comment=postCommentList[index];
  console.log(comment)
  let paramsTemp={
    postCommentId: comment.id,
    getMessageUserId: comment.wxUser.id,
    postId: t.data.postId
  }
  wx.request({
    url: app.globalData.baseUrl+'/post-comment/like',
    data:paramsTemp,
    method: 'PUT',
    header: {
      'content-type': 'application/json' ,
      "Author-Token": token
    },
    success (resp) {
      let result=resp.data;
      if(result.code==200){
        postCommentList[index].isLike = result.data.isLike;
        postCommentList[index].commentLikeCount = result.data.likeCount;
        t.setData({
          postCommentList: postCommentList
        })
     }else{
       wx.showToast({
         title: '操作失败',
         image: '/myicons/error.png',
         duration: 2500
       })
       setTimeout(() => {
        wx.hideToast({
          complete: (res) => {},
        })
      }, 1500);
     }
    }
  })
 },
 //点赞更多回复页面下的评论
 likeChildComment(e){
  let token = wx.getStorageSync('token');
  if(token==''){
    let myUserInfo = wx.getStorageSync('userInfos');
    let user = Object.keys(myUserInfo);
    if(user.length==0 ){
      wx.navigateTo({
        url: '/pages/login/login',
      })
      return;
    }
  }
  const t=this;
  let childCommentList=this.data.childCommentList;
  let index=e.currentTarget.dataset.index;
  let comment=childCommentList[index];
  console.log(comment)
  let paramsTemp={
    postCommentId: comment.id,
    getMessageUserId: comment.wxUser.id,
    postId: t.data.postId
  }
  wx.request({
    url: app.globalData.baseUrl+'/post-comment/like',
    data:paramsTemp,
    method: 'PUT',
    header: {
      'content-type': 'application/json',
      "Author-Token": token
    },
    success (resp) {
      let result=resp.data;
      if(result.code==200){
        childCommentList[index].isLike = result.data.isLike;
        childCommentList[index].commentLikeCount = result.data.likeCount;
        t.setData({
          childCommentList: childCommentList
        })
     }else{
       wx.showToast({
         title: '操作失败',
         image: '/myicons/error.png',
         duration: 2500
       })
       setTimeout(() => {
        wx.hideToast({
          complete: (res) => {},
        })
      }, 1500);
     }
    }
  })
 },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
     // console.log(options);
      let postId=options.postId;
      let tempData={};
      let token = wx.getStorageSync('token');
  if(token==''){
    let anonymousUserId=wx.getStorageSync('anonymousUserId');
   // console.log('anonymousUserId等于：')
   // console.log(anonymousUserId)
    if(anonymousUserId == null || anonymousUserId== ''){
      if(app.globalData.anonymousUserId==null || app.globalData.anonymousUserId==''){
        app.generateAnonymousUserId();
      }
      tempData.anonymousUserId=app.globalData.anonymousUserId;
      wx.setStorageSync('anonymousUserId',app.globalData.anonymousUserId)
    }else{
      tempData.anonymousUserId=anonymousUserId;
      wx.setStorageSync('anonymousUserId',anonymousUserId)
    }
    
  }
      
      request({
        url: '/posts/info/'+postId,
        data: tempData
      }).then(res=>{
        let data=res.data;
        if(data.code==200 ){
          this.setData({
            details: data.data,
            skeloading: false
          })
        }else{
            wx.showToast({
              title: '帖子不见了',
              image: '/myicons/404.png',
              duration: 2500
            })
            setTimeout(() => {
              wx.hideToast({
                complete: (res) => {},
              })
            }, 1500);
        }
       
      })
      this.getComments(postId);
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    this.setData({
      container: () => wx.createSelectorQuery().select('#container'),
    });
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
    let postId=this.data.postId;
    this.queryParams.pageNum=1;
    this.getComments(postId);
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    if(this.data.hasNextPage){
      this.queryParams.pageNum++;
      request({
        url: '/post-comment/list/'+this.data.postId,
        data: this.queryParams
      }).then(res=>{
        this.setData({
          postCommentList: [...this.data.postCommentList,...res.data.data.list],
          hasNextPage: res.data.data.hasNextPage
        })
      })
    }else{
      wx.showToast({
        title: '没有更多数据了',
        image: '../../myicons/nonedata.png',
        duration: 2500
      })
      setTimeout(() => {
        wx.hideToast({
          complete: (res) => {},
        })
      }, 1500);
      
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})