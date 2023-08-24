import React, {useEffect, useState, useRef} from 'react';
import {Button, Input, Layout, theme, message as messageAntd, Modal, Upload, Select, Empty} from 'antd';
import {PlusOutlined, LoadingOutlined, UploadOutlined} from '@ant-design/icons';
import Message from "./components/message";

const {TextArea} = Input;
const {Content, Footer, Sider} = Layout;
const messageType = {
  assistant: "assistant",
  user: "user"
};
const App = () => {
  const {
    token: {colorBgContainer},
  } = theme.useToken();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isModalOpenKey, setIsModalOpenKey] = useState(false);
  const chatWrapperRef = useRef();
  const [onRequest, setOnRequest] = useState(false);
  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState([]);
  const [chatKey, setChatKey] = useState(window.localStorage.getItem('key'))
  const [model, setModel] = useState()
  const [signKey, setSignKey] = useState('')
  const [newKey, setNewKey] = useState('')
  const [keyLoading, setKeyLoading] = useState(false)
  const [streamHosts, setStreamHosts] = useState([
    '//api-chatgpt.yuanshuai.vip',
    '//apiv2-chatgpt.yuanshuai.vip'
  ])

  /**
   * 获取随机推流hosts
   */
  const getRandomStreamHost = ()=> {
    // 生成一个随机索引
    const randomIndex = Math.floor(Math.random() * streamHosts.length);
    // 获取随机元素
    return streamHosts[randomIndex];
  };

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

  const getAnswer = async () => {
    if (!chatKey) {
      messageAntd.error('使用本站请先获取KEY')
      return
    }
    if (!question) {
      messageAntd.error('请输入内容')
      return
    }
    if (onRequest) return;
    const newMessages = [...messages, {
      role: messageType.user,
      content: question
    }];
    setMessages(newMessages);
    // 设置本地缓存
    window.localStorage.setItem("messages", JSON.stringify(newMessages))
    setQuestion("");
    setOnRequest(true);
    let mesStr = ''
    fetch('//chatgpt.yuanshuai.vip/api/chat/sign', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=UTF-8'
      },
      body: JSON.stringify({
        prompt: newMessages.slice(-3),
        chatKey,
        model
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
            role: messageType.assistant,
            content: ''
            }
          ];

          // 获取随机推流url
          const randomStreamHost = getRandomStreamHost();
          const envSource = new EventSource(`${randomStreamHost}/api/chatgpt/stream?signKey=${res.data}`)
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
            window.localStorage.setItem("messages", JSON.stringify([...newMessages1]))
          })
        } catch (e) {
          console.log(e);
        }

      })
  };

  /**
   * 处理下拉选择
   * @param value
   */
  const handleChange = (value) => {
    console.log(`selected ${value}`);
    setModel(value)
  };

  useEffect(() => {
    // 回显示历史对话
    const reMes = window.localStorage.getItem('messages')
    if (reMes) {
      setMessages(JSON.parse(reMes))
    }
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
    if (window.localStorage.getItem('key')) {
      return
    }
    setKeyLoading(true)
    fetch('//chatgpt.yuanshuai.vip/api/key/create', {
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
          setChatKey(res.data)
          window.localStorage.setItem('key', res.data)
          setKeyLoading(false)
        }
      })
  }

  const sedMessage = async (event) => {
    if (event.keyCode === 13 && event.code === "Enter") {
      getAnswer()
    }
  }

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
                  setMessages([])
                  window.localStorage.setItem("messages", "")
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
                  {window.localStorage.getItem('messages') === ""
                      ?
                      <Empty style={{marginTop: 50}}
                        image="/dog.gif"
                        imageStyle={{
                            height: 150,
                        }}
                        description={
                            <span>
                              不会就问吧？😁
                            </span>
                                                                    }
                      >
                      </Empty>
                          :
                          null
                  }
                {
                  messages.map((ele, index) => {
                    return <div key={index}>
                      {
                        ele.role !== 'assistant'
                          ?
                          <div className="bubble right">
                            <a className="avatar"><img
                              src="/ddog.jpeg"
                            />
                            </a>
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
                            <a className="avatar"><img
                              src="/fat.jpg"
                            /></a>
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
                <Select
                    defaultValue="gpt-3.5"
                    style={{
                      width: 100,
                    }}
                    onChange={handleChange}
                    options={[
                      {
                        value: 'gpt-3.5',
                        label: 'gpt-3.5',
                      },
                      {
                        value: 'gpt-4',
                        label: 'gpt-4',
                      },
                    ]}
                />
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
        <br/>
        <br/>
        最后，如果使用有什么问题，那就是故意的(doge).
      </Modal>

      <Modal title="提示" open={isModalOpenKey} onOk={handleOkKey} onCancel={handleCancelKey}>
        <p>请联系管理员获取新的key请妥善保存，key是您使用本网站的唯一对话凭证！</p>
        <p>
          <img style={{width: 423, height: 300}}
               src="/dog.gif" alt=""/>
        </p>
        <p>
          <Input
            value={newKey}
            onChange={(e) => setNewKey(e.target.value)}
            placeholder='请输入您的新KEY'
          />
        </p>
      </Modal>
    </div>

  );
};

export default App;
