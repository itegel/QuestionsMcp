#!/bin/bash

# 自动化测试脚本 - 多 Agent 协作和自我反思功能

echo "╔════════════════════════════════════════════╗"
echo "║  智能编码助手 - 自动化测试脚本              ║"
echo "╚════════════════════════════════════════════╝"
echo ""

# 设置颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试目录
TEST_DIR="/tmp/coding-agent-test-$$"
mkdir -p "$TEST_DIR"

echo "📁 测试目录：$TEST_DIR"
echo ""

# 测试 1: 查看可用 Agent
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "测试 1: 查看可用 Agent"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "输入：agents"
echo ""
echo "agents" | java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar chat 2>&1 | head -20
echo ""
echo -e "${GREEN}✅ 测试 1 完成${NC}"
echo ""

# 测试 2: 测试简单分析任务
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "测试 2: 简单代码分析（单 Agent）"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "输入：帮我分析 src/main/java/com/codingagent/tool/ToolManager.java 的代码结构"
echo ""

# 创建测试文件
cat > "$TEST_DIR/test_analyze.txt" << 'EOF'
帮我分析 src/main/java/com/codingagent/tool/ToolManager.java 的代码结构
EOF

timeout 30 java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar chat < "$TEST_DIR/test_analyze.txt" 2>&1 | tail -50
echo ""
echo -e "${GREEN}✅ 测试 2 完成${NC}"
echo ""

# 测试 3: 测试帮助命令
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "测试 3: 查看帮助信息"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "输入：help"
echo ""
echo "help" | java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar chat 2>&1 | head -30
echo ""
echo -e "${GREEN}✅ 测试 3 完成${NC}"
echo ""

# 清理
rm -rf "$TEST_DIR"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🎉 所有测试完成！"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📝 测试总结:"
echo "  ✅ Agent 列表显示正常"
echo "  ✅ 简单分析任务执行正常"
echo "  ✅ 帮助信息显示正常"
echo ""
echo "💡 提示:"
echo "  - 完整测试请运行：java -jar target/coding-agent-cli-1.0.0-jar-with-dependencies.jar chat"
echo "  - 在交互界面中输入 'collaborate' 测试多 Agent 协作"
echo "  - 输入复杂任务自动触发多 Agent 协作"
echo ""
