import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import 'dayjs/locale/zh-cn';
import zhCN from 'antd/locale/zh_CN';
import 'antd/dist/reset.css';
import {ConfigProvider} from "antd";
import vhCheck from 'vh-check'
vhCheck('browser-address-bar')
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <ConfigProvider locale={zhCN}>
    <App />
    </ConfigProvider>
  </React.StrictMode>

);


