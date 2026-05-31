import MarkdownIt from 'markdown-it'

let md = null
function getMd() {
  if (!md) {
    md = new MarkdownIt({
      html: true,
      linkify: true,
      typographer: true,
      highlight(str, lang) {
        const language = lang || ''
        const escaped = str
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;')
        return `<pre class="language-${language}"><code class="language-${language}">${escaped}</code></pre>`
      },
    })
  }
  return md
}

/**
 * 手动解析 frontmatter（不依赖 gray-matter）
 */
export function parseFrontmatter(raw) {
  const result = { frontmatter: {}, content: raw }
  const match = raw.match(/^---\r?\n([\s\S]*?)\r?\n---\r?\n/)
  if (!match) return result

  const fmRaw = match[1]
  const content = raw.slice(match[0].length)
  const frontmatter = {}

  for (const line of fmRaw.split('\n')) {
    const sep = line.indexOf(':')
    if (sep === -1) continue
    const key = line.slice(0, sep).trim()
    let val = line.slice(sep + 1).trim()
    if ((val.startsWith("'") && val.endsWith("'")) ||
        (val.startsWith('"') && val.endsWith('"'))) {
      val = val.slice(1, -1)
    } else if (val.startsWith('[') && val.endsWith(']')) {
      val = val.slice(1, -1).split(',').map(s => s.trim().replace(/^['"]|['"]$/g, ''))
    }
    frontmatter[key] = val
  }

  result.frontmatter = frontmatter
  result.content = content
  return result
}

export function markdownToHtml(raw) {
  const { frontmatter, content } = parseFrontmatter(raw)
  const html = getMd().render(content)
  return { html, frontmatter }
}
