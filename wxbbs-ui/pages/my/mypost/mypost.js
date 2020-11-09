// pages/my/mypost/mypost.js
import{ request } from "../../../request/index.js"
Page({

  /**
   * 页面的初始数据
   */
  data: {
    postList: [],
    hasNextPage: false
  },
  queryParams:{
    pageNum: 1,
    pageSize: 10,
    "title": ''
  },
   //搜索帖子
   onSearch(e){
    let searchValue=e.detail;
    this.queryParams.pageNum=1;
    this.queryParams.title=searchValue;
    this.getMyPostList();
    
  },
  onClear(e){
    let searchValue=e.detail;
    this.queryParams.pageNum=1;
    this.queryParams.title=searchValue;
    this.getMyPostList();
  },
  //删除帖子
  delClick(e){
    console.log(e)
    const t=this;
    let postId=e.currentTarget.dataset.postid;
    wx.showModal({
      title: '温馨提示',
      content: '确认删除该帖子吗?',
      success (res) {
        if (res.confirm) {
          request({
            url:"/my/posts/"+postId,
            method: 'DELETE'
          }).then(res=>{
            let code=res.data.code;
            if(code==200){
              t.queryParams.pageNum=1;
              t.setData({
                postList:[]
              })
              t.getMyPostList();
              wx.showToast({
                title: '删除成功',
                image: '/myicons/success.png'
              })
            }
          })
        } else if (res.cancel) {
          
        }
      }
    })
  },
//我的帖子
 getMyPostList(){
  request({
    url: '/my/posts',
    data: this.queryParams
  }).then(res=>{
    this.setData({
      postList: res.data.data.list,
      hasNextPage: res.data.data.hasNextPage
    })
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
    this.getMyPostList();
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
    this.queryParams.pageNum=1;
    this.setData({
      postList:[]
    });
    this.getMyPostList();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    if(this.data.hasNextPage){
      console.log("是否有一页:"+this.data.hasNextPage)
      this.queryParams.pageNum++;
      request({
        url: '/my/posts',
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
        image: '../../../myicons/nonedata.png'
      })
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})