const { path } = require('@vuepress/utils')

module.exports = {
  lang: 'zh-CN',
  title: '文档 - Y20',
  description: '鲲擎流水线（Y20）是一个自动化流水线系统，通过编排流水线实现流程的标准化、自动化！',
  base: '/docs/',
  theme: path.resolve(__dirname, './theme'),
  themeConfig: {
    domain: 'https://y20.work/docs',
    contributors: false,
    sidebar: [
      {
        text: '入门',
        children: [
          '/',
          '/quick-start.md',
        ],
      },
      {
        text: '基础概念',
        children: [
          '/basic/agent.md',
          '/basic/pipeline.md',
          '/basic/step.md',
          '/basic/variable.md',
          '/basic/file.md',
          '/basic/webhook.md',
          '/basic/queue.md',
          '/basic/template.md',
          '/basic/team.md',
          '/basic/monitor.md',
        ],
      },
      {
        text: '操作指南',
        children: [
          '/guide/agent-join.md',
          '/guide/pipeline-edit.md',
          '/guide/pipeline-run.md',
          '/guide/pipeline-template.md',
        ],
      },
      {
        text: '应用示例',
        children: [
        ],
      },
      {
        text: '常见问题',
        children: [
        ],
      },
      {
        text: '服务条款',
        link: '/terms-of-service.md',
      },
    ],
  },
  plugins: [
    [
      'vuepress-plugin-seo',
      {
        customMeta: (add, context) => {
          add('keywords', '鲲擎流水线,Y20,持续部署,持续集成,流水线,自动化运维,CI,CD,devops')
        },
    }
    ]
  ]
}