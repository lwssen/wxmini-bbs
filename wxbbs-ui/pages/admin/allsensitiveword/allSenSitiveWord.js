// pages/admin/allsensitiveword/allSenSitiveWord.js
import { request } from "../../../request/index.js";
Page({

  /**
   * 页面的初始数据
   */
  data: {
    sensitiveWordList: [],
    searchValue:null,
    show:false,
    addShow:false,
    hasNextPage: false,
    sensitiveWord:'',
    changeValue:'',
    index:''
  },
  queryParams:{
    pageNum:1,
    pageSize:10,
  },
  //获取敏感词列表
  getSensitiveWords(){
    const t=this;
    request({
      url:'/system/sensitiveWords',
      data: this.queryParams
    }).then(resp=>{
      let data=resp.data;
        if(data.code==200){
          t.setData({
            sensitiveWordList: resp.data.data.list,
            hasNextPage: resp.data.data.hasNextPage
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
      changeValue: newValue
    })
   },
   showChange(e){
    // console.log(e);
     let index=e.currentTarget.dataset.index;
     let sensitiveWordList=this.data.sensitiveWordList;
     let temp=sensitiveWordList[index];
     console.log(temp)
     let sensitiveWord=temp.sensitiveWord;
     let sensitiveWordIndex=temp.index;
      this.setData({
        show:true,
        changeValue: sensitiveWord,
        index: sensitiveWordIndex
      })
   },
   change(e){
    // console.log(e)
     const t=this;
     let sensitiveWord={};
     sensitiveWord.index=this.data.index;
     sensitiveWord.changeValue=this.data.changeValue;
  
     request({
       url:'/system/sensitiveWords/change',
       data: sensitiveWord,
       method: 'PUT'
     }).then(resp=>{
       let data=resp.data;
     if(data.code==200){
         t.setData({
           sensitiveWordList:[],
           show: false,
           changeValue:''
         });
         t.queryParams.pageNum=1;
         t.getSensitiveWords();
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
     let sensitiveWord={};
     sensitiveWord.sensitiveWord=this.data.changeValue;
     request({
       url:'/system/sensitiveWords/add',
       data: sensitiveWord,
       method: 'POST'
     }).then(resp=>{
       let data=resp.data;
     if(data.code==200){
         t.setData({
           sensitiveWordList:[],
           addShow: false,
           changeValue:''
         });
         t.queryParams.pageNum=1;
         t.getSensitiveWords();
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
     let sensitiveWordList=this.data.sensitiveWordList;
     let temp=sensitiveWordList[index];
     let queryParams=this.queryParams;
     wx.showModal({
       title: '温馨提示',
       content: '确定删除吗?',
       success (resp) {
         if (resp.confirm) {
           request({
             url:'/system/sensitiveWords/deleted',
             method: 'PUT',
             data: temp
           }).then(resp=>{
             let data=resp.data;
             if(data.code==200){
                 t.setData({
                   sensitiveWordList:[],
                 })
                 t.queryParams.pageNum=1;
                 t.getSensitiveWords();
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
     this.setData({ 
       show: false,
       addShow: false
       });
   },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
      this.getSensitiveWords();
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
      sensitiveWordList: []
    })
    this.getSensitiveWords();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {
    const t=this;
    if(this.data.hasNextPage){
      this.queryParams.pageNum++;
      request({
        url: '/system/sensitiveWords',
        data: this.queryParams
      }).then(res=>{
        t.setData({
          sensitiveWordList: [...this.data.sensitiveWordList,...res.data.data.list],
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