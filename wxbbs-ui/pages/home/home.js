// pages/home/home.js
 import{ request } from "../../request/index.js"

 //获取应用实例
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    typeId: 1,
    value: '',
    navBarList: [],
    postList:[],
    active: 'home',
    hasNextPage: false,
    messageCount: '',
    userInfo: {},
    hasUserInfo: false,
    optionValue: '',
    orderValue: '',
    option: [
      { text: '默认', value: '' },
      { text: '阅读最多', value: 'view_count' },
      { text: '评论最多', value: 'comment_count' },
      { text: '点赞最多', value: 'like_count' },
    ],
  },
  queryParams:{
    pageNum: 1,
    pageSize: 10,
    title: '',
    typeId: ''
  },
  //按类型排序
  optionChange(e){
    let orderValue=e.detail;
    this.queryParams.pageNum=1;
    this.queryParams.orderValue=orderValue;
    this.setData({
      postList: [],
      orderValue: orderValue
    })
    this.getPostList();
  },
  //搜索帖子
  onSearch(e){
    let searchValue=e.detail;
    this.queryParams.pageNum=1;
    this.queryParams.title=searchValue;
    this.getPostList();
    
  },
  onClear(e){
    let searchValue=e.detail;
    this.queryParams.pageNum=1;
    this.queryParams.title=searchValue;
    this.getPostList();
  },
  handlerChange(e){
  // console.log(e)
  },


//分类列表
getPostTypes(){
  request({
    url: '/post-types'})
    .then(result=>{
      this.setData({
               navBarList: result.data.data,
             })
  })
 },
  //帖子列表
  getPostList(){
    request({
      url: '/posts',
      data: this.queryParams
    }).then(res=>{
      this.setData({
        postList: res.data.data.list,
        hasNextPage: res.data.data.hasNextPage
      })
         }),
    //关闭下拉刷新
    wx.stopPullDownRefresh();
  },
 //帖子详情
 postInfo(e){
  const t=this;
  let index=e.currentTarget.dataset.index;
  let postList=this.data.postList;
  let userInfo=postList[index];
  let postId=userInfo.id;
  wx.navigateTo({
    url: '/pages/postinfo/postInfo?postId='+postId,
  })

 },
 //点击分类查询帖子列表
  onClick(e){
    let typeId=e.detail.name;
   if(typeId===1){
     typeId=''
   }
   this.queryParams.typeId=typeId;
   this.queryParams.pageNum=1;
   this.queryParams.orderValue=this.data.orderValue;
   this.setData({
    postList: [],
    typeId: typeId
  })
   request({
     url: '/posts',
     data: this.queryParams
   }).then(res=>{
    this.setData({
      postList: res.data.data.list
    })
   })
  },
  //点赞帖子
 like(e){
  let token = wx.getStorageSync('token');
  console.log(token)
  if(token==''){
    app.checkLogin();
    return;
  }
  const t=this;
  let index=e.currentTarget.dataset.index;
  let postList=this.data.postList;
 let userInfo=postList[index];
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
     'content-type': 'application/json' ,
     "Author-Token": token
   },
   success (resp) {
     let result=resp.data;
     if(result.code==200){
       postList[index].isLike=result.data.isLike;
       postList[index].likeCount=result.data.likeCount;
       t.setData({
         postList: postList
       })
    }else{
      wx.showToast({
        title: result.message,
        image: '/myicons/error.png'
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
    const t=this;
    console.log("是否已登录："+app.globalData.isLogin)
    var myUserInfo = wx.getStorageSync('userInfos');
    let isLogin=app.globalData.isLogin;
   // console.log("app.globalData.isLogin等于")
   // console.log(isLogin)
    if(myUserInfo !=null && myUserInfo !='' && !isLogin){
       console.log("已存在用户openid,重新获取token")
      this.getAcessToken(myUserInfo);
      app.globalData.isLogin=true;
      app.getUserMessage();
      this.setData({
        messageCount: app.globalData.messageCount
      })
    }
    request({
      url: '/post-types'
    }).then(
      result=>{
        let data=result.data;
       if(data.code==445){
        console.log("token过期后,重新获取token")
        this.getAcessToken(myUserInfo);
       }
    })
    
setTimeout(function() {
    t.getPostTypes();
    t.getPostList();
}, 1500)
// this.getPostTypes();
// this.getPostList();
   
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
 
  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    // console.log("执行了onReady方法")
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
    // console.log("下拉刷新")
    if(this.data.navBarList.length==0){
      this.getPostTypes();
    }
      this.setData({
        postList: []
      })
      this.queryParams.pageNum=1;
      this.getPostList();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
      // console.log("页面触底")
      if(this.data.hasNextPage){
        this.queryParams.pageNum++;
        request({
          url: '/posts',
          data: this.queryParams
        }).then(res=>{
          this.setData({
            postList: [...this.data.postList,...res.data.data.list],
            hasNextPage: res.data.data.hasNextPage
          })
        })
      }else{
        wx.showToast({
          title: '没有更多数据了',
          image: '../../myicons/nonedata.png'
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