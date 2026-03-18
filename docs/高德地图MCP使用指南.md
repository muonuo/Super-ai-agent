# 高德地图 MCP 使用指南

## 概述

本项目已集成高德地图 MCP 服务（`@amap/amap-maps-mcp-server`），AI 可以直接调用高德地图的 15 个工具，实现路径规划、天气查询、地点搜索等功能。

## 配置说明

### MCP 服务器配置

位置：`src/main/resources/mcp-servers.json`

```json
{
  "mcpServers": {
    "amap-maps": {
      "command": "npx.cmd",
      "args": [
        "-y",
        "@amap/amap-maps-mcp-server"
      ],
      "env": {
        "AMAP_MAPS_API_KEY": "你的高德地图 API Key"
      }
    }
  }
}
```

### 应用配置

位置：`src/main/resources/application.yaml`

```yaml
spring:
  ai:
    mcp:
      client:
        stdio:
          servers-configuration: classpath:mcp-servers.json
```

## 高德地图 MCP 工具列表

### 位置服务
| 工具名 | 功能 | 示例 |
|--------|------|------|
| `maps_geocode` | 地理编码（地址→经纬度） | "把北京市海淀区中关村大街转为经纬度" |
| `maps_regeocode` | 逆地理编码（经纬度→地址） | "39.9042,116.4074 是什么地方" |
| `maps_ip_location` | IP 定位 | "114.114.114.114 是哪个城市" |

### 生活服务
| 工具名 | 功能 | 示例 |
|--------|------|------|
| `maps_weather` | 天气查询 | "北京今天天气怎么样" |

### 路径规划
| 工具名 | 功能 | 示例 |
|--------|------|------|
| `maps_bicycling` | 骑行路径规划 | "从北京站地铁站骑车到清华大学" |
| `maps_walking` | 步行路径规划 | "从故宫走到景山公园怎么走" |
| `maps_driving` | 驾车路径规划 | "从北京到上海开车怎么走" |
| `maps_transit` | 公交路径规划 | "从西二旗到上地坐地铁怎么走" |
| `maps_distance` | 距离测量 | "北京到上海有多远" |

### POI 搜索
| 工具名 | 功能 | 示例 |
|--------|------|------|
| `maps_search_text` | 关键词搜索 | "搜索北京市的医院" |
| `maps_search_around` | 周边搜索 | "查找我附近的餐厅" |
| `maps_search_detail` | POI 详情 | "查询故宫的详细信息" |

### 出行服务
| 工具名 | 功能 | 示例 |
|--------|------|------|
| `maps_static_map` | 生成专属地图 | "生成北京的地图" |
| `maps_navigation` | 导航到目的地 | "导航到首都国际机场" |
| `maps_taxi` | 打车 | "叫车去北京南站" |

## API 接口

### 同步接口

**URL**: `POST /api/ai/love_app/sync/maps`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| message | String | 是 | 用户消息 |
| chatId | String | 是 | 会话 ID（用于多轮对话） |

**示例**:
```bash
curl -X GET "http://localhost:8123/api/ai/love_app/sync/maps?message=从北京到上海的驾车路线&chatId=test123"
```

### 流式接口（推荐）

**URL**: `GET /api/ai/love_app/stream/maps/sse`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| message | String | 是 | 用户消息 |
| chatId | String | 是 | 会话 ID |

**示例**:
```bash
curl -X GET "http://localhost:8123/api/ai/love_app/stream/maps/sse?message=从北京到上海的驾车路线&chatId=test123"
```

## 使用示例

### 1. 路径规划

```bash
# 驾车路线
curl "http://localhost:8123/api/ai/love_app/sync/maps?message=从北京驾车到上海怎么走&chatId=trip1"

# 骑行路线
curl "http://localhost:8123/api/ai/love_app/sync/maps?message=从中关村骑车到五道口怎么走&chatId=bike1"

# 公交路线
curl "http://localhost:8123/api/ai/love_app/sync/maps?message=从西二旗到上地坐地铁怎么走&chatId=subway1"
```

### 2. 地点搜索

```bash
# 关键词搜索
curl "http://localhost:8123/api/ai/love_app/sync/maps?message=搜索北京市海淀区的三甲医院&chatId=hospital1"

# 周边搜索
curl "http://localhost:8123/api/ai/love_app/sync/maps?message=查找我附近的咖啡馆&chatId=cafe1"
```

### 3. 天气查询

```bash
curl "http://localhost:8123/api/ai/love_app/sync/maps?message=北京今天天气怎么样&chatId=weather1"
```

### 4. 地址解析

```bash
# 地理编码
curl "http://localhost:8123/api/ai/love_app/sync/maps?message=把北京市海淀区中关村大街转为经纬度&chatId=geo1"

# 逆地理编码
curl "http://localhost:8123/api/ai/love_app/sync/maps?message=39.9042,116.4074 是什么地方&chatId=revgeo1"
```

## 代码集成

### 方式一：使用现有接口（推荐）

直接使用 `/api/ai/love_app/sync/maps` 或 `/api/ai/love_app/stream/maps/sse` 接口，无需额外代码。

### 方式二：在 Controller 中新增专用接口

```java
@RestController
@RequestMapping("/api/date-assistant")
public class DateAssistantController {

    @Resource
    private LoveApp loveApp;

    /**
     * 约会路线规划
     */
    @PostMapping("/plan-route")
    public String planDateRoute(@RequestBody DateRouteRequest request) {
        return loveApp.doChatWithMcp(
            "帮我规划从" + request.from + "到" + request.to + "的" + request.mode + "路线",
            request.chatId
        );
    }

    /**
     * 查找约会地点
     */
    @PostMapping("/find-places")
    public String findDatePlaces(@RequestBody PlaceSearchRequest request) {
        return loveApp.doChatWithMcp(
            "在" + request.location + "附近搜索" + request.type + "，适合约会",
            request.chatId
        );
    }
}
```

### 方式三：封装为高级工具

创建自定义工具类，内部调用 MCP 工具，提供更高级的接口。

## 测试方法

1. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

2. **调用接口**
   ```bash
   curl "http://localhost:8123/api/ai/love_app/sync/maps?message=从北京到上海的驾车路线&chatId=test1"
   ```

3. **查看日志**
   控制台会输出 MCP 工具调用的详细日志。

## 注意事项

1. **API Key**: 确保 `mcp-servers.json` 中的 `AMAP_MAPS_API_KEY` 有效
2. **Node.js**: 需要安装 Node.js（用于运行 npx）
3. **网络**: 首次使用会下载 `@amap/amap-maps-mcp-server` 包
4. **对话记忆**: 使用 `chatId` 参数可以实现多轮对话

## 常见问题

### Q: MCP 服务启动失败？
A: 检查以下几点：
- Node.js 是否正确安装
- `mcp-servers.json` 配置是否正确
- API Key 是否有效

### Q: 工具调用无响应？
A: 检查：
- 网络连接是否正常
- 查看应用日志，确认 MCP 服务是否成功启动
- 确认 API Key 有足够的配额

### Q: 如何查看 MCP 工具调用日志？
A: 在 `application.yaml` 中添加：
```yaml
logging:
  level:
    org.springframework.ai.mcp: DEBUG
```
