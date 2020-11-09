// pages/admin/allpostype/allPostType.js
import { request } from "../../../request/index.js";
Page({

  /**
   * 页面的初始数据
   */
  data: {
    active: 0,
    postTypeList: [],
    show:false,
    addShow:false,
    hasNextPage: false,
    typeId:'',
    typeName:"aaaa",
    newTypeName:"",
    postType:{}
  },
  queryParams:{
    pageNum:1,
    pageSize:10,
  },
  //获取类型列表
  getPostTypes(e){
    const t=this;
      request({
        url:'/post-types',
        data: this.queryParams
      }).then(resp=>{
        let data=resp.data;
        if(data.code==200){
          t.setData({
            postTypeList: resp.data.data,
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
  getChangeValue(e){
   let newValue=e.detail;
   this.setData({
     newTypeName: newValue
   })
  },
  showChange(e){
   // console.log(e);
    let index=e.currentTarget.dataset.index;
    let postTypeList=this.data.postTypeList;
    let temp=postTypeList[index];
    console.log(temp)
    let typeName=temp.typeName;
    let typeId=temp.typeId;
     this.setData({
       show:true,
       typeName: typeName,
       typeId: typeId
     })
  },
  change(e){
   // console.log(e)
    const t=this;
    let postType=this.data.postType;
    postType.typeId=this.data.typeId;
    postType.typeName=this.data.newTypeName;
    this.setData({
      postType
    })
    request({
      url:'/system/post-types',
      data: postType,
      method: 'PUT'
    }).then(resp=>{
      let data=resp.data;
    if(data.code==200){
        t.setData({
          postTypeList:[],
          show: false,
          newTypeName:''
        });
        t.queryParams.pageNum=1;
        t.getPostTypes();
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
  showAdd(e){
    this.setData({
      addShow:true
    })
  },
  add(e){
    const t=this;
    let postType={};
    postType.typeName=this.data.newTypeName;
    this.setData({
      postType:postType
    })
    request({
      url:'/system/post-types',
      data: postType,
      method: 'PUT'
    }).then(resp=>{
      let data=resp.data;
    if(data.code==200){
        t.setData({
          postTypeList:[],
          addShow: false,
          newTypeName:''
        });
        t.queryParams.pageNum=1;
        t.getPostTypes();
        wx.showToast({
          title: '新增成功',
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
  //删除类型
  delType(e){
    const t=this;
    let index=e.currentTarget.dataset.index;
    let postTypeList=this.data.postTypeList;
    let temp=postTypeList[index];
    let typeId=temp.typeId;
    wx.showModal({
      title: '温馨提示',
      content: '确定删除该类型吗?',
      success (resp) {
        if (resp.confirm) {
          request({
            url:'/system/post-types/'+typeId,
            method: 'DELETE'
          }).then(resp=>{
            let data=resp.data;
            if(data.code==200){
                t.setData({
                  postTypeList:[]
                })
                t.queryParams.pageNum=1;
                t.getPostTypes();
                wx.showToast({
                  title: '删除成功',
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
        } else if (resp.cancel) {
          console.log('用户点击取消')
        }
      }
    })

    
  },
  onClose() {
    this.setData({ show: false });
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
   this.getPostTypes();
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
        postTypeList: []
      })
      this.getPostTypes();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    const t=this;
    if(this.data.hasNextPage){
      this.queryParams.pageNum++;
      request({
        url: '/post-types',
        data: this.queryParams
      }).then(res=>{
        t.setData({
          postTypeList: [...this.data.postTypeList,...res.data.data.list],
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