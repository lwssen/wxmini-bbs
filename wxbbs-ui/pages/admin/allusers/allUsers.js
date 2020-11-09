// pages/admin/allusers/allUsers.js
import { request } from "../../../request/index.js";
Page({

  /**
   * 页面的初始数据
   */
  data: {
    pageNum:0,
    pageSize:10,
    active: 0,
    hasNextPage: false,
    allUserList: [],
    searchValue: null,
    userTotalCount: 0,
    postTotalCount:0,
    adminValue: '',
    userStateValue: '',
    setAdminOption: [
        { text: '管理员', value: 1 },
        { text: '普通用户', value: 2 },
    ],
    setDel: [
      { text: '允许', value: true },
      { text: '禁止', value: false},
  ],
    userStatusOption: [
        { text: '正常', value: 0 },
        { text: '禁用', value: 1 },
        { text: '禁言', value: 2 },
    ],
  },
  queryParams:{
    pageNum: 1,
    pageSize: 10
  },
  //获取用户列表
   getAllUsers(){
     const t=this;
     request({
       url: '/system/all/users',
       data: this.queryParams
     }).then(resp=>{
       let data=resp.data;
       if(data.code==200){
        t.setData({
          allUserList: resp.data.data.list,
          hasNextPage: resp.data.data.hasNextPage
          })
       }else{
         wx.showToast({
           title: data.message,
           image:'/myicons/error.png'
         })
         setTimeout(() => {
             wx.hideToast({
             complete: (res) => {},
              })
        }, 1500);
       }
     })
   },
   // 获取用户和帖子总数
 getUserAndPostTotalCount(){
   const t=this;
    request({
      url: '/system/total/counts'
    }).then(resp=>{
      let data=resp.data;
      if(data.code==200){
        t.setData({
          userTotalCount: resp.data.data.userTotalCount,
          postTotalCount: resp.data.data.postTotalCount
        })
      }
    })
 } , 
   //搜索用户
onSearch(e){
  let searchValue=e.detail;
  let queryParams=this.queryParams;
  queryParams.pageNum=1;
  queryParams.userName=searchValue;
  this.setData({
      queryParams
  })
  this.getAllUsers();
  
},
onClear(e){
  let searchValue=e.detail;
  let queryParams=this.queryParams;
  queryParams.pageNum=1;
  queryParams.userName=searchValue;
  this.setData({
      queryParams
  })
  this.getAllUsers();
},

//修改用户状态
changeUserState(e){
  let index=e.detail.value;
  let value=this.data.userStatusOption[index].value;
  let userIndex=e.currentTarget.dataset.index;
  let allUserList=this.data.allUserList;
  let userId=allUserList[userIndex].id;
  const t=this;
    request({
    url: '/system/change/user/status',
    data: {
      id: userId,
      state: value
    },
    method: 'PUT'
  }).then(resp=>{
    let data=resp.data;
    if(data.code==200){
      allUserList[userIndex].state=value;
        t.setData({
          allUserList
        })
        wx.showToast({
          title: '修改成功',
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
          image:'/myicons/error.png'
        })
        setTimeout(() => {
             wx.hideToast({
             complete: (res) => {},
              })
        }, 1500);
    }
  })
 
},
setAdmin(e){
  let index=e.detail.value;
  let roleId=this.data.setAdminOption[index].value;
  let userIndex=e.currentTarget.dataset.index;
  let allUserList=this.data.allUserList;
  let userId=allUserList[userIndex].id;
  const t=this;
    request({
    url: '/system/set/admin',
    data: {
      id: userId,
      roleId: roleId
    },
    method: 'PUT'
  }).then(resp=>{
    let data=resp.data;
    if(data.code==200){
      allUserList[userIndex].roleId=roleId;
        t.setData({
          allUserList
        })
        wx.showToast({
          title: '修改成功',
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
          image:'/myicons/error.png'
        })
        setTimeout(() => {
             wx.hideToast({
             complete: (res) => {},
              })
        }, 1500);
    }
  })
},
setAdd(e){
  let index=e.detail.value;
  let delId=this.data.setDel[index].value;
  let userIndex=e.currentTarget.dataset.index;
  let allUserList=this.data.allUserList;
  let userId=allUserList[userIndex].id;
  const t=this;
    request({
    url: '/system/set/del',
    data: {
      id: userId,
      isPush: delId
    },
    method: 'PUT'
  }).then(resp=>{
    let data=resp.data;
    if(data.code==200){
      allUserList[userIndex].isPush=delId;
        t.setData({
          allUserList
        })
        wx.showToast({
          title: '修改成功',
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
          image:'/myicons/error.png'
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
      this.getUserAndPostTotalCount();
      this.getAllUsers();
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
        allUserList: []
      })
      this.getAllUsers();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    const t=this;
    if(this.data.hasNextPage){
      this.queryParams.pageNum++;
      console.log("第几页："+this.queryParams.pageNum)
      request({
        url: '/system/all/users',
        data: this.queryParams
      }).then(res=>{
        t.setData({
          allUserList: [...this.data.allUserList,...res.data.data.list],
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