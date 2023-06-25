import React, {useEffect, useState, useRef} from 'react';
import {Button, Input, Layout, theme, message as messageAntd, Modal,Upload} from 'antd';
import {PlusOutlined, LoadingOutlined, UploadOutlined} from '@ant-design/icons';
import Message from "./components/message";

const {TextArea} = Input;
const {Content, Footer, Sider} = Layout;
const messageType = {
  answer: "answer",
  question: "question"
};
const App = () => {
  const {
    token: {colorBgContainer},
  } = theme.useToken();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isModalOpenKey, setIsModalOpenKey] = useState(false);
  const [isModalOpenSeeMsg, setIsModalOpenSeeMsg] = useState(false)

  const showModal = () => {
    setIsModalOpen(true);
  };

  const handleOk = () => {
    setIsModalOpen(false);
  };

  const handleCancel = () => {
    setIsModalOpen(false);
  };

  const showModalKey = () => {
    setIsModalOpenKey(true);
  };

  const handleOkKey = () => {
    setChatKey(newKey)
    window.localStorage.setItem('key', newKey)
    setIsModalOpenKey(false);
  };

  const handleCancelKey = () => {
    setIsModalOpenKey(false);
  };



  const handleOkSeeMsg = () => {
    setHistoricalData([])
    setIsModalOpenSeeMsg(false);
  };


  const chatWrapperRef = useRef();

  const [onRequest, setOnRequest] = useState(false);
  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState([]);
  const [chatKey, setChatKey] = useState(window.localStorage.getItem('key'))
  const [signKey, setSignKey] = useState('')
  const [newKey, setNewKey] = useState('')
  const [historicalData, setHistoricalData] = useState([])
  const [keyLoading, setKeyLoading] = useState(false)

  const getAnswer = async () => {
    if (!chatKey) {
      messageAntd.error('使用本站请先获取key')
      return
    }
    if (!question) {
      messageAntd.error('请输入内容')
      return
    }
    if (onRequest) return;
    const newMessages = [...messages, {
      type: messageType.question,
      content: question
    }];
    setMessages(newMessages);
    setQuestion("");
    setOnRequest(true);
    let mesStr = ''
    fetch('//yyss.life/api/chat/sign', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      body: JSON.stringify({
        prompt: question.trim(),
        chatKey,
        conversationId: signKey || ''
      })
    })
      .then(res => res.json())
      .then(res => {
        console.log(res);
        if (res.code !== 200) {
          setOnRequest(false);
          messageAntd.warning(res.msg)
          if (res.code === 302) {
            showModalKey()
          }
          return;
        }
        setSignKey(res.data)
        try {
          const newMessages1 = [...newMessages, {
            type: messageType.answer,
            content: ''
            }
          ];
          const envSource = new EventSource(`//yyss.life/api/chat/stream?signKey=${res.data}`)
          console.log(envSource);
          envSource.addEventListener('error', function(event) {
            messageAntd.warning('服务波动，请重试！')
            setOnRequest(false);
            envSource.close()
          });
          envSource.addEventListener('message', (e) => {
            const data = JSON.parse(e.data)
            if (data.content === null) {
              setOnRequest(false);
              envSource.close()
              return
            }
            mesStr += data.content
            newMessages1.forEach((ele, index) => {
              if (index === newMessages1.length - 1) {
                ele.content = mesStr
              }
            })
            setMessages([...newMessages1]);
          })
        } catch (e) {
          console.log(e);
        }

      })
  };

  useEffect(() => {
    // 监听myDiv元素的子元素变化
    const observer = new MutationObserver(() => {
      // 当myDiv元素的高度增加后，滚动到底部
      chatWrapperRef.current.scrollTop = chatWrapperRef.current.scrollHeight;
    });
    observer.observe(chatWrapperRef.current, {childList: true, subtree: true});
    return () => {
      observer.disconnect();
    };
  }, []);

  const getKey = () => {
    //  http://43.207.180.252:8090/key/create
    if (window.localStorage.getItem('key')) {
      return
    }
    setKeyLoading(true)
    fetch('//yyss.life/api/key/create', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      }
    })
      .then(res => res.json())
      .then(res => {
        console.log(res);
        if (res.code === 200) {
          messageAntd.success('获取成功')
          setChatKey(res.data.userKey)
          window.localStorage.setItem('key', res.data.userKey)
          setKeyLoading(false)
        }
      })
  }

  const sedMessage = async (event) => {
    if (event.keyCode === 13 && event.code === "Enter") {
      getAnswer()
    }
  }


  const saveJson = () => {
    if (!messages.length > 2) {
      return
    }
    // 创建一个Date对象
    let today = new Date();

// 获取年份
    let year = today.getFullYear();

// 获取月份（注意：月份从0开始，所以需要加1）
    let month = today.getMonth() + 1;

// 获取日期
    let day = today.getDate();

// 输出结果
    console.log(year + "-" + month + "-" + day);
    // 将数组转换为 JSON 字符串
    const jsonStr = JSON.stringify(messages);

// 创建一个 Blob 对象
    const blob = new Blob([jsonStr], {type: 'application/json'});

// 创建一个下载链接
    const url = URL.createObjectURL(blob);

// 创建一个 <a> 标签并设置下载链接
    const link = document.createElement('a');
    link.href = url;
    link.download = `${messages[0].content}-${year}-${month}-${day}.json`;

// 将 <a> 标签添加到页面并触发点击事件
    document.body.appendChild(link);
    link.click();

// 释放资源
    URL.revokeObjectURL(url);
  }


  const props = {
    showUploadList: false,
    maxCount: 1,
    accept: '.json',
    name: 'file',
    beforeUpload:(file) => {
      return new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsText(file);
        reader.onload = (event) => {
          const fileContent = event.target.result;
          const jsonData = JSON.parse(fileContent);
          console.log(jsonData); // 输出 JSON 数据
          setHistoricalData(jsonData)
          resolve(false)
        };
      });
    },
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text)
      .then(() => {
        messageAntd.info('文本已复制到剪贴板')
      })
      .catch(() => {
        console.error('无法复制文本到剪贴板！');
      });
  }


  return (
    <div>

      <Layout
        style={{
          overflow: 'auto',
          height: `calc(100vh - var(--browser-address-bar, 0px)`,
        }}
      >
        <Sider
          breakpoint="lg"
          collapsedWidth="0"
          onBreakpoint={(broken) => {
            console.log(broken);
          }}
          onCollapse={(collapsed, type) => {
            console.log(collapsed, type);
          }}
        >
          <div
            className='newPair'
          >
            <div>
              <Button
                block
                onClick={() => {
                  setSignKey('')
                  saveJson()
                  setMessages([])
                }}
              >
                <PlusOutlined/>
                开始新对话
              </Button>
            </div>
            <div className='getkey'>
              <Button loading={keyLoading} onClick={() => {
                if (chatKey) {
                  copyToClipboard(chatKey)
                  messageAntd.info(chatKey)
                  return
                }
                getKey()
              }} block>{chatKey ? '查看 | 复制KEY' : '获取KEY'}</Button>
            </div>
            <div className='getkey'>
              <Button
                block
                onClick={() => {
                  saveJson()
                }}
              >
                保存当前对话
              </Button>
            </div>
            <div className='getkey'>
              <Button
                block
                onClick={() => {
                  setIsModalOpenSeeMsg(true)
                }}
              >
                查看历史对话
              </Button>
            </div>
            <div className='getkey'>
              <Button
                block
                onClick={() => {
                  window.localStorage.removeItem('key')
                  setChatKey('')
                }}
              >
                清除KEY
              </Button>
            </div>
          </div>

        </Sider>
        <Layout>
          <Content style={{margin: '10px 10px 0'}}>
            <div style={{padding: 10, height: '100%', background: colorBgContainer}}>
              <div ref={chatWrapperRef} className='message_box'>
                {
                  messages.map((ele, index) => {
                    return <div key={index}>
                      {
                        ele.type !== 'answer'
                          ?
                          <div className="bubble right">
                            <a className="avatar" href><img
                              src="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202106%2F22%2F20210622154903_3c36a.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1682005825&t=24cfbac8798a6e98e1814e08f3d02518"
                              alt/></a>
                            <div className="wrap">
                              <div style={{
                                maxWidth: `calc(100% - 5px)`
                              }} className="content">
                                <Message content={ele.content}/>
                              </div>
                            </div>
                          </div>
                          :
                          <div className="bubble left">
                            <a className="avatar" href><img
                              src="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202106%2F09%2F20210609081952_51ef5.thumb.1000_0.jpg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1682005840&t=c4e15c2e0df93c6fdf6498a615383c4e"
                              alt/></a>
                            <div className="wrap">
                              <div style={{
                                maxWidth: `calc(100% - 5px)`

                              }} className="content">
                                {
                                  ele.content
                                    ?
                                    <Message content={ele.content}/>
                                    :
                                    <LoadingOutlined style={{color: 'rgba(0,0,0,.45)'}}/>
                                }

                              </div>
                            </div>
                          </div>
                      }
                    </div>
                  })
                }
              </div>
              <div className='message_input'>
                <div className='message_input_text'>

                  <TextArea
                    onKeyDown={(e) => {
                      sedMessage(e)
                    }}
                    placeholder='发送消息给AI'
                    style={{
                      width: '100%'
                    }}
                    disabled={onRequest}
                    value={question}
                    onChange={(e) => setQuestion(e.target.value)}
                    autoSize={{minRows: 2, maxRows: 2}}
                  />
                  {/*<Input*/}
                  {/*  placeholder='发送消息给AI'*/}
                  {/*  style={{*/}
                  {/*    width: '100%'*/}
                  {/*  }}*/}
                  {/*  disabled={onRequest}*/}
                  {/*  value={question}*/}
                  {/*  onChange={(e) => setQuestion(e.target.value)}*/}
                  {/*  suffix={*/}
                  {/*    <Tooltip title="点击发送">*/}
                  {/*      {*/}
                  {/*        onRequest && <LoadingOutlined/>*/}
                  {/*      }*/}
                  {/*    </Tooltip>*/}
                  {/*  }*/}
                  {/*/>*/}
                </div>
                <div className='message_input_send'>
                  <Button
                    onClick={() => {
                      getAnswer()
                    }}
                  >
                    {
                      onRequest ? <LoadingOutlined/>
                        : '发送'
                    }
                  </Button>
                </div>
                <div className='message_input_key'>
                  <img onClick={() => {
                    setIsModalOpenKey(true);
                  }} src="/7hrqaokPWn.png" alt=""/>
                </div>

              </div>
            </div>
          </Content>
          <Footer
            style={{
              textAlign: 'center',
              padding: 10,
              fontSize: 10,
              cursor: "pointer"
            }}
            onClick={() => {
              showModal()
            }}
          >
            本站点，仅供学习 AI 使用，使用前请知晓 免责申明
          </Footer>
        </Layout>
      </Layout>

      <Modal title="声明" open={isModalOpen} onOk={handleOk} onCancel={handleCancel}>
        本网站 仅是一款虚拟聊天机器人，其提供的信息和建议仅供参考，不代表具有法律或专业的性质。
        <br/>
        <br/>
        本网站 所给出的信息均来自公开可得的信息来源，不保证其准确性、完整性、实时性或适用性。
        <br/>
        <br/>
        由于 本网站 无法控制用户的输入和操作，因此使用本工具过程中产生的任何后果均由用户个人承担。
        <br/>
        <br/>
        本网站 不承担任何因信息获取、使用或依赖 本网站 所提供的信息而产生的任何直接、间接、偶然、特殊、衍生、惩罚性或因违约而产生的损失。
        <br/>
        <br/>
        本网站 保留权利随时更改本免责声明。请在使用前定期检查以获取最新更新的版权声明。
        <br/>
        <br/>
        在您开始使用 本网站 之前，请确保您已完全理解以上所有条款，并接受其中的所有条款。如果您对本免责声明有任何疑问，请勿使用该服务。
      </Modal>

      <Modal title="提示" open={isModalOpenKey} onOk={handleOkKey} onCancel={handleCancelKey}>
        <p>请联系客服获取新的key请妥善保存，key是您使用本网站的唯一对话凭证！</p>
        <p>
          <img style={{width: 423, height: 300}}
               src="/wxcode.jpg" alt=""/>
        </p>
        <p>
          <Input
            value={newKey}
            onChange={(e) => setNewKey(e.target.value)}
            placeholder='您的新key'
          />
        </p>
      </Modal>


      <Modal
        style={{
          top: 20,
        }}
        title="查看历史对话"
        open={isModalOpenSeeMsg}
        onOk={handleOkSeeMsg}
        onCancel={handleOkSeeMsg}
      >
        <Upload {...props}>
          <Button icon={<UploadOutlined />}>
            上传历史对话JSON文件
          </Button>
        </Upload>
        <div
          style={{
            height: 450,
            overflowY: "auto"
          }}
        >
          {
            historicalData.map((ele, index) => {
              return <div key={index}>
                {
                  ele.type !== 'answer'
                    ?
                    <div className="bubble right">
                      <a className="avatar" href><img
                        src="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202106%2F22%2F20210622154903_3c36a.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1682005825&t=24cfbac8798a6e98e1814e08f3d02518"
                        alt/></a>
                      <div className="wrap">
                        <div style={{
                          maxWidth: `calc(100% - 5px)`
                        }} className="content">
                          <Message content={ele.content}/>
                        </div>
                      </div>
                    </div>
                    :
                    <div className="bubble left">
                      <a className="avatar" href><img
                        src="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202106%2F09%2F20210609081952_51ef5.thumb.1000_0.jpg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1682005840&t=c4e15c2e0df93c6fdf6498a615383c4e"
                        alt/></a>
                      <div className="wrap">
                        <div style={{
                          maxWidth: `calc(100% - 5px)`

                        }} className="content">
                          {
                            ele.content
                              ?
                              <Message content={ele.content}/>
                              :
                              <LoadingOutlined style={{color: 'rgba(0,0,0,.45)'}}/>
                          }

                        </div>
                      </div>
                    </div>
                }
              </div>
            })
          }
        </div>
      </Modal>
    </div>

  );
};

export default App;