# HomeKit Android Client

This is an Android client application for controlling indoor lights via a local area network (LAN).

The basic principle is simple: devices are discovered using mDNS, and a web server on the control device is accessed to display the GUI. The light control part is done by sending commands through the WebSocket protocol.

This is purely a personal interest project and does not consider many issues such as user authentication, public access, or rate-limiting control commands.
# Screenshot:

<img src=./screenshot.jpg width=30% />

# Known Bugs:

- Device discovery only works on the 192.168.0.0/16 subnet.
- The control device occasionally crashes. Since a reboot resets the relay state, the lights may temporarily flicker during startup.

# Potential Improvements:

- Replace WebSocket protocol with MQTT protocol.
- Use native controls instead of web pages.
- Add user authentication.
- Limit control command QPS (queries per second).
- ...

---

# HomeKit的安卓客户端

这是一个通过局域网控制室内灯的安卓客户端程序

原理很简单，通过mDNS进行设备发现，然后访问控制设备上搭建的web服务器展现GUI。灯控部分是通过websocket协议发送命令实现的

这只是出于个人兴趣的项目，没有考虑包含用户验证、公网访问、控制频率限制等诸多问题

# 截图：

<img src=./screenshot.jpg width=30% />

# 已知的bug：

- 必须在192.168.0.0/16网段下才能进行设备发现
- 控制设备有时会死机。由于重启会复位继电器状态，在开机这段时间会产生灯光暂时的闪烁

# 可以进行的改进：

- 使用MQTT协议替换websocket协议
- 使用原生控件代替Web页面
- 添加用户验证
- 限制控制命令的qps
- ...

> 和某些知名项目名称重复了，实际本项目与那些产品毫无关系
> 这倒不是为了蹭热度，而是当时真没想出来什么好名字，随便想了一个就也懒得改了
