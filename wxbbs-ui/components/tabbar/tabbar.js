// components/tabbar/tabbar.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    tabActive:{
      type: String,
      value: ''
    },
    messageCount:{
      type: Number,
      value: ''
    }
    
  },

  /**
   * 组件的初始数据
   */
  data: {
    active: '',
    value: '',
    icon: {
      home: '../../myicons/shouye_1.png',
      homeActive: '../../myicons/shouye.png',
      push: '../../myicons/fabu1.png',
      pushActive: '../../myicons/fabu.png',
      reward: '../../myicons/dashang1.png',
      rewardActive: '../../myicons/dashang.png',
      message: '../../myicons/xiaoxi1.png',
      messageActive: '../../myicons/xiaoxi.png',
      my: '../../myicons/wode1.png',
      myActive: '../../myicons/wode.png'
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    onChange(e) {
      // event.detail 的值为当前选中项的索引
      // console.log("子组件事件开始")
      // console.log(e)
      let index=e.detail;
      //子组件向父组件传递数据
      this.triggerEvent("itemChange",{index})
      this.setData({ 
        active: this.properties.tabActive
      });
      // console.log("active等于："+this.data.active)
      // console.log("event等于："+JSON.stringify(e))
      // console.log("aaa等于："+this.properties.tabActive)
      // console.log("子组件事件结束")
      if(index==='push'){
        wx.redirectTo({
         // url: '/pages/push/pushPost',
         url: '/pages/editor/editor',
        });
      }else if(index=='reward'){
        wx.redirectTo({
          url: '/pages/reward/reward'
        })
      }else if(index=='message'){
        wx.redirectTo({
          url: '/pages/message/message'
        })
      }else if(index=='my'){
        wx.redirectTo({
          url: '/pages/my/my'
        })
      }else{
        wx.redirectTo({
          url: '/pages/home/home'
        })
      }
    },

  }
})
