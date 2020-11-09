   let ajaxTimes=0;
    //获取应用实例 Author-Token
   export const request=(params)=>{
    var token = wx.getStorageSync('token');
    // console.log("request获取用户token等于："+token)
    let header={...params.header};
    header["Author-Token"]=token;
     ajaxTimes++;
    //全局显示加载中 效果
    wx.showLoading({
        title: "加载中",
        mask: true
      });
  //定义公共的url
 // const baseUrl="http://192.168.0.102:9080/backapi";
 // const baseUrl="http://192.168.2.192:9080/backapi";
  const baseUrl="https://www.sssen.top/backapi";
 return new Promise((resolve,reject)=>{
   wx.request({
       ...params,
       header:header,
       url: baseUrl+params.url,
       success:(result)=>{
      
         resolve(result);
      
        
       },
       fail:(err)=>{
         reject(err);
       
       },
       complete: () =>{
         ajaxTimes--;
        // 关闭全局加载中效果
        if(ajaxTimes===0){
          setTimeout(function () {
            wx.hideLoading()
        }, 1000);
        }
        //关闭下拉刷新
    wx.stopPullDownRefresh();
   
           }
     })
 })
}