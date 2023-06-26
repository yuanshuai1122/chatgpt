

import React from "react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import {Prism as SyntaxHighlighter} from "react-syntax-highlighter";
import {atomDark} from "react-syntax-highlighter/dist/cjs/styles/prism";

const Index = (props) => {

  return  <ReactMarkdown
    children={props.content}
    remarkPlugins={[[remarkGfm, {singleTilde: false}]]}
    components={{
      code({node, inline, className, children, ...props}) {
        const match = /language-(\w+)/.exec(className || 'language-js')
        return !inline && match ? (
          <SyntaxHighlighter
            children={String(children).replace(/\n$/, '')}
            style={atomDark} language={match[1]} PreTag="div" {...props}
          />
        ) : (<code className={className} {...props}>{children}</code>)
      }
    }}
  />
}

export default Index
