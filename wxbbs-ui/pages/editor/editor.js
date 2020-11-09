import { request } from "../../request/index.js";
//获取应用实例
var app = getApp()
Page({
  data: {
    formats: {},
    readOnly: false,
    placeholder: '开始输入...',
    editorHeight: 500,
    keyboardHeight: 0,
    isIOS: false,
    title: '',
    inputContent: null,
    active: 'push',
    messageCount: ''
  },
  readOnlyChange() {
    // console.log("触发readOnlyChange()方法")
    this.setData({
      readOnly: !this.data.readOnly
    })
  },
 
  onLoad() {
   //  console.log("触发onLoad()方法")
   console.log(app)
     this.checkLogin();
    const platform = wx.getSystemInfoSync().platform
    const isIOS = platform === 'ios'
    this.setData({ isIOS})
    const that = this
    this.updatePosition(0)
    let keyboardHeight = 0
    wx.onKeyboardHeightChange(res => {
      if (res.height === keyboardHeight) return
      const duration = res.height > 0 ? res.duration * 1000 : 0
      keyboardHeight = res.height;
      setTimeout(() => {
        wx.pageScrollTo({
          scrollTop: 0,
          success() {
            that.updatePosition(keyboardHeight)
            that.EditorContext.scrollIntoView()
          }
        })
      }, duration)

    })
  },
  updatePosition(keyboardHeight) {
    // console.log("触发updatePosition方法")
    const toolbarHeight = 50
    const { windowHeight, platform } = wx.getSystemInfoSync()
    let editorHeight = keyboardHeight > 0 ? (windowHeight - keyboardHeight - toolbarHeight) : windowHeight
    this.setData({ editorHeight, keyboardHeight })
  },
  calNavigationBarAndStatusBar() {
    // console.log("触发calNavigationBarAndStatusBar()方法")
    const systemInfo = wx.getSystemInfoSync()
    const { statusBarHeight, platform } = systemInfo
    const isIOS = platform === 'ios'
    const navigationBarHeight = isIOS ? 44 : 48
    return statusBarHeight + navigationBarHeight
  },
  onEditorReady() {
    // console.log("触发onEditorReady()方法")
    const that = this
    wx.createSelectorQuery().select('#editor').context(function (res) {
      that.EditorContext = res.context
    }).exec()
  },
 
  blur() {
    // console.log("触发blur()方法")
    this.EditorContext.blur()
  },
  format(e) {
    // console.log("触发format(e)方法")
    let { name, value } = e.target.dataset
    if (!name) return
    // // console.log('format', name, value)
    this.EditorContext.format(name, value)

  },
  onStatusChange(e) {
    // console.log("触发onLoad()方法")
    const formats = e.detail
    
    this.setData({ formats })
  },
  insertDivider() {
    // console.log("触发insertDivider()方法")
    this.EditorContext.insertDivider({
      success: function () {
        // console.log('insert divider success')
      }
    })
  },
  clear() {
    //// console.log("触发clear()方法")
    this.EditorContext.clear({
      success: function (res) {
     //   // console.log("clear success")
      }
    })
  },
  removeFormat() {
    // console.log("触发removeFormat()方法")
    this.EditorContext.removeFormat()
  },
  insertDate() {
    // console.log("触发insertDate()方法")
    const date = new Date()
    const formatDate = `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`
    this.EditorContext.insertText({
      text: formatDate
    })
  },
  insertImage() {
    // console.log("触发insertImage()方法")
    let token = wx.getStorageSync('token');
    let url=app.globalData.baseUrl;
    const that = this;
    const t=this;
    let tempImageFilePaths=[];
    let imageUrl='';
    wx.chooseImage({
      count: 1,
      // 可以指定是原图还是压缩图，默认二者都有
     // sizeType: ['original', 'compressed'],
      // 可以指定来源是相册还是相机，默认二者都有
    //  sourceType: ['album', 'camera'],
      success: function (res) {
       tempImageFilePaths=res.tempFilePaths;
        that.EditorContext.insertImage({
          src: imageUrl,
          data: {
           
          },
          width: '100%',
          success: function () {
            // console.log("富文本上传图片")
            wx.uploadFile({
              url: url+'/posts/image/upload', 
              filePath: tempImageFilePaths[0],
              name: 'file',
              header: {"Author-Token": token},
              formData: {},
              success (ressucess){
               let data = ressucess.data
                let result=JSON.parse(data);
                if(result.code==200){
                  imageUrl=JSON.parse(data).data;
                  // // console.log(data)
                  // // console.log(JSON.stringify(data))
                 // console.log("图片路径等于："+imageUrl)
                   t.myInsertImage(imageUrl);
                }else{
                  t.setData({
                    inputContent: null
                  })
                }
              }
            })

          }
        })
      
      }
    })
  },
 

  onClickLeft() {
    wx.reLaunch({
      url: '/pages/home/home'
    })
  },
 
  //获取标题
  onChange(e){
    // console.log(e)
    let value=e.detail;
    this.setData({
      title: value
    })
   // // console.log("标题等于："+this.data.title)
  
  },
  //获取富文本内容
getContent(){
    const t=this;
      //获取富文本内容
      t.EditorContext.getContents({
        success:(resEditor)=>{
             console.log("富文本内容");
             console.log(resEditor.html);
            let content=resEditor.html;
            t.setData({
              inputContent: content
            })
          },
        fail:(resfail)=>{
          // console.log("fail：" + resfail);
          }
      })
  },
 //再次插入图片
 myInsertImage(imageUrl){
  const that=this;
  const t=this;
  that.EditorContext.insertImage({
    src: imageUrl,
    data: {
    },
    width: '100%',
    success: function () {
      //获取富文本内容
      t.EditorContext.getContents({
        success:(resEditor)=>{
            // console.log("富文本内容");
            // console.log(resEditor.html);
            let temp=resEditor.html;
            let index=temp.lastIndexOf('<p');
            // console.log("index等于："+index);
            let content=temp.slice(0,index);
            console.log("截取的富文本内容："+content);
            t.setData({
              inputContent: content
            })
          },
        fail:(resfail)=>{
          // console.log("fail：" + resfail);
          }
      })
    }
  })
},
//发布帖子
onClickRight() {
  console.log("发布帖子app")
  console.log(app)
  //app.checkLogin();
  const t=this;
 // this.checkLogin();
  if(t.data.inputContent==null){
  //  console.log("富文本内容为空")
    t.getContent();
  }else{
    t.getContent();
  }
  wx.showModal({
    title: '',
    content: '确认发布内容吗?',
    success (res) {
      if (res.confirm) {
        const postComment = {
          title: t.data.title,
          postCotent: t.data.inputContent
      };
        request({
          url:'/posts/push',
          data: postComment,
          method: 'POST'
        }).then(resp=>{
          let data=resp.data;
          if(data.code==200){
           t.setData({
             title: null,
             inputContent: null
             })
             wx.showToast({
              title: '发布成功',
              image:'/myicons/success.png'
            })
            setTimeout(() => {
                 wx.hideToast({
                 complete: (res) => {},
                  })
            }, 1500);
             wx.reLaunch({
              url: '/pages/home/home'
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
      } else if (res.cancel) {
        //// console.log('用户点击取消')
      }
    }
  }) 
},
//检查是否登录
  checkLogin() {
    console.log("执行了checkLogin()方法");
    var myUserInfo = wx.getStorageSync('userInfos');
    var user = Object.keys(myUserInfo);
    console.log(user);
    console.log(app.globalData.isLogin);
    if(user.length==0 ){
     console.log("未登录");
     wx.showModal({
      title: '提示',
      content: '当前处于未登录状态，不允许此操作,是否前往登录页进行登录？',
      cancelText: "取消登录",
      cancelColor: "#FE2E2E",
      confirmText: "前往登录",
      confirmColor: "#07c160",
      success: (res) => {
        if (res.confirm) {
          // console.log('用户点击确定')
          wx.navigateTo({
            url: '/pages/login/login',
          })
          return;
        } else if (res.cancel) {
          // console.log('用户点击取消')
         
        }
        
      },
    })
    }
    console.log("已登录");
  } 
})
