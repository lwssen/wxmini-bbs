import { request } from "../../../request/index.js";

// pages/admin/allposts/allposts.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    navBarList: [],
    allPostList: [],
    searchValue: null,
    typeId:1,
    value1: '',
    value2: '',
    hasNextPage: false,
    option1: [

    ],
    option2: [
        { text: '删除', value: true},
        { text: '正常', value: false },
    ],
    typeIndex: 0,
    delIndex: 0,
    postTypeDto: {},
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
    orderValue: '',
    title: ''
  },
  //根据类型查询帖子
  onClick(e){
    const t= this;
    let typeId=e.detail.name;
    let temp=this.queryParams;
    temp.orderValue=this.data.orderValue;
    temp.pageNum=1;
   
    if(typeId===1){
      typeId=''
    }
    temp.typeId=typeId;
    t.setData({
      allPostList: []
    })
    request({
      url: '/posts',
      data: temp
    }).then(resp=>{
      let data = resp.data;
      let allPostList=data.data.list;
      if (data.code === 200) {
          //业务逻辑
          t.setData({
            allPostList
      })
      }else {
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
// 修改帖子所属类型
  bindPickerChange(e) {
    console.log(e)
    let typeId=this.data.navBarList[e.detail.value].typeId;
    let typeName=this.data.navBarList[e.detail.value].typeName;
    let allPostList = this.data.allPostList;
    let idx=e.currentTarget.dataset.index;
    allPostList[idx].typeName = typeName
    let postId=allPostList[idx].id
    let userId=allPostList[idx].wxUser.id;
    let isDeleted=allPostList[idx].isDeleted;
    let postTypeDto=this.data.postTypeDto;

    postTypeDto.typesId=typeId;
    postTypeDto.postId=postId;
    postTypeDto.getMessageUserId=userId;
    postTypeDto.isDeleted=isDeleted
    
    const t=this;
    request({
      url: '/system/change/post-type',
      data: postTypeDto,
      method: 'PUT'
    }).then(resp=>{
      let data = resp.data;
      if (data.code === 200) {
          //业务逻辑
          console.log("typeId等于:"+typeId);
          if (typeId !=null && typeId ==1){
              console.log("typeId等于:"+"移除元素");
              allPostList.splice(idx,1);
          }else {
              allPostList[idx].typesId=typeId;
              allPostList[idx].typeName=typeName;
          }
          t.setData({
            allPostList,
            postTypeDto
          })
         // t.refreshData();
         wx.showToast({
           title: "转移成功",
           image: '/myicons/success.png'
         })
         setTimeout(() => {
                      wx.hideToast({
                        complete: (res) => {},
                      })
                    }, 1500);
      }else {
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
  //是否删除帖子
  bindPickerChange2(e) {
    let allPostList = this.data.allPostList;
    let idx=e.currentTarget.dataset.index;
    let postTypeDto=this.data.postTypeDto;
    let postId=allPostList[idx].id
    let userId=allPostList[idx].wxUser.id;
    let isDeleted=allPostList[idx].isDeleted;
    //postTypeDto.typesId=typeId;
    postTypeDto.postId=postId;
    postTypeDto.getMessageUserId=userId;
    let isDeleted2=this.data.option2[e.detail.value].value;
    postTypeDto.isDeleted=isDeleted2;
    console.log('picker发送选择改变，携带值为'+isDeleted2);
    console.log(e)
    
    const t=this;
    request({
      url: '/system/delete/post',
      data: postTypeDto,
      method: 'PUT'
    }).then(resp=>{
      let data = resp.data;
      if (data.code === 200) {
          //业务逻辑
          allPostList[idx].isDeleted=isDeleted2
          t.setData({
            postTypeDto,
            allPostList
          })
        wx.showToast({
           title: "修改成功",
           image: '/myicons/success.png'
         })
         setTimeout(() => {
                      wx.hideToast({
                        complete: (res) => {},
                      })
                    }, 1500);
      }else {
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
//获取帖子分类
  getNavBarList(e) {
    const t=this;
      request({
        url: '/post-types'
      }).then(res=>{
        this.setData({
          navBarList: res.data.data,
          
        })
      })
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
  //获取帖子列表
  getPostList(e) {
    request({
      url: '/system/all/posts',
      data: this.queryParams
    }).then(res=>{
      let data = res.data;
      if (data.code === 200) {
        this.setData({
          allPostList: res.data.data.list,
          hasNextPage:res.data.data.hasNextPage
         })
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
getOptions(e) {
  request({
    url: '/post-types/options'
  }).then(res=>{
    this.setData({
      option1: res.data.data
     })
  })
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
//查看所有评论跳转
toJump(e){
 let postId=e.currentTarget.dataset.postid;
 wx.navigateTo({
   url: '/pages/admin/allpostcomments/allPostComments?postId='+postId,
 })
},
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
   this.getNavBarList();
   this.getPostList();
   this.getOptions();
    
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
    console.log("下拉刷新")
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
    console.log("页面触底")
    const t=this;
    if(this.data.hasNextPage){
      this.queryParams.pageNum++;
      request({
        url: '/system/all/posts',
        data: t.queryParams
      }).then(res=>{
        t.setData({
          allPostList: [...t.data.allPostList,...res.data.data.list],
          hasNextPage: res.data.data.hasNextPage
        })
        console.log(res.data)
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