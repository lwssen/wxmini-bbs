// pages/admin/allpostcomments/allPostComments.js
import { request } from "../../../request/index.js";
Page({

  /**
   * 页面的初始数据
   */
  data: {
    allPostCommentList:[],
    childCommentList: [],
    hasNextPage: false,
    showChildContent: false,
    setContentIndex: null,
    setTopPushIndex: null,
    inputValue: '',
    isMaster: true,
    option2: [
      { text: '删除', value: true},
      { text: '正常', value: false },
  ],
    globalId: null
  },
  queryParams:{
    pageNum: 1,
    pageSize: 10
    
  },
  //获取评论列表
  getComments(postId){
    const t=this;
   // console.log("postId等于："+postId)
      request({
        url: '/system/admin/list/'+postId,
        data: this.queryParams
      }).then(resp=>{
        let data=resp.data;
        if(data.code==200){
            t.setData({
              allPostCommentList: data.data.list,
              hasNextPage: data.data.hasNextPage,
              postId: postId
            })
        }else{

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
         isMaster: false
       })
    }
  })
 },
 onClose() {
   let isMaster=this.data.isMaster;
   console.log("关闭的isMaster等于："+isMaster)
   if(isMaster){
     this.onCloseMaster();
   }else{
     this.onCloseChild();
   }
},
onClose2() {
  this.setData({
    showChildContent: false
  })
},
onCloseManyComment() {
  this.setData({ 
    showChildContent: false,
   });
},
 
onCloseMaster() {
  this.setData({ 
    showSetTop: false,
    showShielding: false,
    isMaster: true
   });
},
onCloseChild() {
  this.setData({ 
    showSetTop: false,
    showShielding: false,
    isMaster: false
   });
},

getChangeValue(e){
  let newValue=e.detail;
 // console.log("评论内容等于："+newValue)
  this.setData({
    inputValue: newValue
  })
 },

 showChildManyComment(e){
 //  console.log("显示更多回复")
   const t=this;
   let index=e.currentTarget.dataset.index;
   let data=this.data.allPostCommentList[index];
   let tempQueryParams={
     id: data.id,
    postId: data.postId,
  }
t.getChildManyComment(tempQueryParams);
   this.setData({
    showChildContent: true,
    childManyCommentId: data.id,
    
   }) 
 },
 //弹出设置屏蔽文字框
 showShielding(e){
 // console.log("屏蔽输入框显示")
 // console.log(e)
  let index=e.currentTarget.dataset.id;
  let isMaster=e.currentTarget.dataset.ismaster;
  this.setData({
    showShielding: true,
    setContentIndex: index,
    isMaster:isMaster
  })
//  console.log("isMaster等于："+this.data.isMaster);
 },
 
 //设置屏蔽文字
 setContent(){
  let isMaster=this.data.isMaster;
  console.log("发布的isMaster等于："+this.data.isMaster);
  if(isMaster){
    this.masterSetContent();
   // console.log("主评论")
  }else{
    this.childSetContent();
  //  console.log("子评论")
  }
  
 },
 masterSetContent(){ 
  const t=this;
  let value=this.data.inputValue;
  let index=this.data.setContentIndex;
  let id=this.data.allPostCommentList[index].id;
  let postId=this.data.allPostCommentList[index].postId;
  let userId=this.data.allPostCommentList[index].wxUser.id;
  let allPostCommentList=this.data.allPostCommentList; 
 let paramsValue={
   id: id,
   commentContent: value,
   getMessageUserId: userId,
   postId: postId,
 }
 request({
   url: '/system/comment/shielding',
   data: paramsValue,
   method: 'PUT'
 }).then(resp=>{
   let data=resp.data;
       if(data.code==200){
         allPostCommentList[index].commentContent=data.data;
           t.setData({
             inputValue: null,
             allPostCommentList: allPostCommentList
           })
           t.onCloseMaster();
           wx.showToast({
             title: '操作成功',
             image: '/myicons/success.png'
           })
       }else{
         wx.showToast({
           title: data.message,
           image: '/myicons/error.png'
         })
       }
 })
},
 childSetContent(){
  const t=this;
  let value=this.data.inputValue;
  let index=this.data.setContentIndex;
  let id=this.data.childCommentList[index].id;
  let postId=this.data.childCommentList[index].postId;
  let userId=this.data.childCommentList[index].wxUser.id;
  let childCommentList=this.data.childCommentList; 
 // console.log("子回评论的id："+id)
 let paramsValue={
   id: id,
   commentContent: value,
   getMessageUserId: userId,
   postId: postId,
 }
 request({
   url: '/system/comment/shielding',
   data: paramsValue,
   method: 'PUT'
 }).then(resp=>{
   let data=resp.data;
       if(data.code==200){
        childCommentList[index].commentContent=data.data;
           t.setData({
             inputValue: null,
             childCommentList: childCommentList
           })
           t.onCloseChild();
           wx.showToast({
             title: '操作成功',
             image: '/myicons/success.png'
           })
       }else{
         wx.showToast({
           title: data.message,
           image: '/myicons/error.png'
         })
       }
 })
},
//弹出设置置顶排序框
 showSetTop(e){
 // console.log("置顶输入框显示")
  let index=e.currentTarget.dataset.id;
  let isMaster=e.currentTarget.dataset.ismaster;
  console.log(e)
  this.setData({
    showSetTop: true,
    setTopPushIndex: index,
    isMaster: isMaster
  })
 },

 //设置置顶排序
 setTopPush(){
   let isMaster=this.data.isMaster;
   if(isMaster){
     this.masterSetTopPush();
   }else{
     this.childSetTopPush();
   }
 },
 masterSetTopPush(){
  const t=this;
  let value=this.data.inputValue;
  let index=this.data.setTopPushIndex;
  let allPostCommentList=this.data.allPostCommentList;
 // console.log(t.data.allPostCommentList[index])
  let id=this.data.allPostCommentList[index].id;
  
 let paramsValue={
   id: id,
   orderNumber: value
 }
 request({
   url: '/system/comment/top',
   data: paramsValue,
   method: 'PUT'
 }).then(resp=>{
   let data=resp.data;
       if(data.code==200){
           t.setData({
             inputValue: null,
           })
           t.onCloseMaster();
          wx.showToast({
            title: '操作成功',
            image: '/myicons/success.png',
            duration: 2500
          })
          setTimeout(() => {
            wx.hideToast({
              complete: (res) => {},
            })
          }, 1500);

          
       }else{
         wx.showToast({
           title: data.message,
           image: '/myicons/error.png'
         })
       }
 })
 },
 childSetTopPush(){
  const t=this;
  let value=this.data.inputValue;
  let index=this.data.setTopPushIndex;
  let childCommentList=this.data.childCommentList;
  console.log(t.data.childCommentList[index])
  let id=this.data.childCommentList[index].id;
 // console("子回评论的id："+id)
  
 let paramsValue={
   id: id,
   orderNumber: value
 }
 request({
   url: '/system/comment/top',
   data: paramsValue,
   method: 'PUT'
 }).then(resp=>{
   let data=resp.data;
       if(data.code==200){
           t.setData({
             inputValue: null, 
           })
           t.onCloseChild();
          wx.showToast({
            title: '操作成功',
            image: '/myicons/success.png',
            duration: 2500
          })
          setTimeout(() => {
            wx.hideToast({
              complete: (res) => {},
            })
          }, 1500);

       }else{
         wx.showToast({
           title: data.message,
           image: '/myicons/error.png'
         })
         setTimeout(() => {
                      wx.hideToast({
                        complete: (res) => {},
                      })
                    }, 1500);
       }
 })
 },
 //删除评论
 bindPickerChange(e){
   const t=this;
  let index=e.currentTarget.dataset.index;
  let option2Index=e.detail.value;
  let value=this.data.option2[option2Index].value;
  let allPostCommentList=this.data.allPostCommentList
  let comment=this.data.allPostCommentList[index];
  let getmessageUserId=comment.wxUser.id;
  let postId=comment.postId;
  let id=comment.id;
  let paramsVlue={
    getMessageUserId: getmessageUserId,
    postId: postId,
    isDeleted:  value
  }
  request({
    url: '/system/post-comment/'+id,
    data: paramsVlue,
    method: 'PUT'
  }).then(resp=>{
    let data=resp.data;
    if(data.code==200){
      allPostCommentList[index].isDeleted=value;
        t.setData({
          allPostCommentList: allPostCommentList
        })
        wx.showToast({
         title: '操作成功',
         image: '/myicons/success.png'
       })
       setTimeout(() => {
                    wx.hideToast({
                      complete: (res) => {},
                    })
                  }, 1500);
    }else{
      wx.showToast({
        title: data.message,
        image: '/myicons/error.png'
      })
      setTimeout(() => {
                    wx.hideToast({
                      complete: (res) => {},
                    })
                  }, 1500);
    }
  })
 },
 bindPickerChange2(e){
  console.log(e);
  const t=this;
  let index=e.currentTarget.dataset.index;
  let option2Index=e.detail.value;
  let value=this.data.option2[option2Index].value;
  let childCommentList=this.data.childCommentList
  let comment=this.data.childCommentList[index];
  let getmessageUserId=comment.wxUser.id;
  let postId=comment.postId;
  let id=comment.id;
  let paramsVlue={
    getMessageUserId: getmessageUserId,
    postId: postId,
    isDeleted:  value
  }
  request({
    url: '/system/post-comment/'+id,
    data: paramsVlue,
    method: 'PUT'
  }).then(resp=>{
    let data=resp.data;
    if(data.code==200){
      childCommentList[index].isDeleted=value;
        t.setData({
          childCommentList: childCommentList
        })
        wx.showToast({
         title: '操作成功',
         image: '/myicons/success.png'
       })
       setTimeout(() => {
                    wx.hideToast({
                      complete: (res) => {},
                    })
                  }, 1500);
    }else{
      wx.showToast({
        title: data.message,
        image: '/myicons/error.png'
      })
      setTimeout(() => {
                    wx.hideToast({
                      complete: (res) => {},
                    })
                  }, 1500);
    }
  })
 },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let postId=options.postId;
    this.setData({
      globalId: postId
    })
    this.getComments(postId);
      
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
     let postId=this.data.globalId;
     this.queryParams.pageNum=1;
     this.getComments(postId)
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
          allPostCommentList: [...this.data.allPostCommentList,...res.data.data.list],
          hasNextPage: res.data.data.hasNextPage
        })
      })
    }else{
      wx.showToast({
        title: '没有更多数据了',
        image: '/myicons/nonedata.png'
      })
      setTimeout(() => {
                    wx.hideToast({
                      complete: (res) => {},
                    })
                  }, 1500);
    }
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})