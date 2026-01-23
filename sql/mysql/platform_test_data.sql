/*
 开放平台模块测试数据
 
 说明：
 1. 提供基本的测试数据，方便快速验证功能
 2. client_secret 应该是加密存储的，这里仅用于演示
 3. 实际使用时需要替换为真实的业务数据
*/

-- ----------------------------
-- Records of platform_client
-- ----------------------------
BEGIN;
INSERT INTO `platform_client` VALUES 
(1, 'client_demo_001', 'secret_encrypted_demo_001', '演示客户端A', 'demo_client_a', NULL, '这是一个演示客户端，用于测试', '演示科技有限公司', '91110000123456789X', '张三', 'zhangsan@demo.com', '13800138000', 1, 1, 1000, 100000, 0, 0, 1000000, 0, 10000, '["192.168.1.100","192.168.1.101"]', 'https://demo.com/webhook', NULL, NULL, 'admin', NOW(), '', NOW(), b'0'),
(2, 'client_demo_002', 'secret_encrypted_demo_002', '演示客户端B', 'demo_client_b', NULL, '这是另一个演示客户端', '测试科技有限公司', '91110000987654321X', '李四', 'lisi@test.com', '13900139000', 1, 1, 500, 50000, 0, 0, 500000, 0, 10000, NULL, NULL, NULL, NULL, 'admin', NOW(), '', NOW(), b'0');
COMMIT;

-- ----------------------------
-- Records of platform_api
-- ----------------------------
-- 说明：
--   1. api_code 采用权限标识格式：platform:资源:操作（如 platform:order:create）
--   2. api_path 只存储业务路径（如 /order/create）
-- 客户端访问方式：
--   推荐: api.xxx.com/order/create
--   或者: xxx.com/open-api/order/create（需配置路由映射）
BEGIN;
INSERT INTO `platform_api` VALUES 
(1, 'platform:order:create', '创建订单', '/order/create', 'POST', 'order', '创建订单接口', 1, 100, 1, 10, 'admin', NOW(), '', NOW(), b'0'),
(2, 'platform:order:query', '查询订单', '/order/query', 'GET', 'order', '查询订单详情', 1, 200, 1, 2, 'admin', NOW(), '', NOW(), b'0'),
(3, 'platform:product:list', '商品列表', '/product/list', 'GET', 'product', '获取商品列表', 1, 500, 1, 1, 'admin', NOW(), '', NOW(), b'0'),
(4, 'platform:user:info', '用户信息', '/user/info', 'GET', 'user', '获取用户基本信息', 1, 1000, 0, 0, 'admin', NOW(), '', NOW(), b'0');
COMMIT;

-- ----------------------------
-- Records of platform_client_api (授权关系)
-- ----------------------------
BEGIN;
-- 客户端A 的授权（自定义价格）
INSERT INTO `platform_client_api` VALUES 
(1, 'client_demo_001', 1, 1, NULL, NULL, b'1', 5, NULL, NULL, 'admin', NOW(), '', NOW(), b'0'),  -- 创建订单：自定义5分
(2, 'client_demo_001', 2, 1, NULL, NULL, b'0', NULL, NULL, NULL, 'admin', NOW(), '', NOW(), b'0'),  -- 查询订单：默认2分
(3, 'client_demo_001', 3, 1, NULL, NULL, b'0', NULL, NULL, NULL, 'admin', NOW(), '', NOW(), b'0'),  -- 商品列表：默认1分
(4, 'client_demo_001', 4, 1, NULL, NULL, b'0', NULL, NULL, NULL, 'admin', NOW(), '', NOW(), b'0');  -- 用户信息：免费

-- 客户端B 的授权（默认价格）
INSERT INTO `platform_client_api` VALUES 
(5, 'client_demo_002', 1, 1, NULL, NULL, b'0', NULL, NULL, NULL, 'admin', NOW(), '', NOW(), b'0'),  -- 创建订单：默认10分
(6, 'client_demo_002', 2, 1, NULL, NULL, b'0', NULL, NULL, NULL, 'admin', NOW(), '', NOW(), b'0');  -- 查询订单：默认2分
-- 注意：client_demo_002 没有授权 product.list 和 user.info，因此无法访问这两个 API（白名单机制）
COMMIT;

-- ----------------------------
-- 验证查询
-- ----------------------------

-- 1. 查看所有客户端
-- SELECT * FROM platform_client;

-- 2. 查看所有 API
-- SELECT * FROM platform_api;

-- 3. 查看授权关系
-- SELECT 
--     ca.id,
--     ca.client_id,
--     c.client_name,
--     a.api_code,
--     a.api_name,
--     a.api_path,
--     CASE 
--         WHEN ca.is_custom_price = b'1' THEN ca.custom_price 
--         ELSE a.default_price 
--     END AS final_price,
--     ca.status AS auth_status
-- FROM platform_client_api ca
-- LEFT JOIN platform_client c ON ca.client_id = c.client_id
-- LEFT JOIN platform_api a ON ca.api_id = a.id
-- ORDER BY ca.client_id, a.api_code;

-- 4. 验证权限（示例：client_demo_001 可以访问哪些 API）
-- SELECT 
--     a.api_code AS '权限标识',
--     a.api_name AS 'API名称',
--     a.api_path AS 'API路径',
--     a.http_method AS '方法',
--     CASE 
--         WHEN ca.is_custom_price = b'1' THEN CONCAT(ca.custom_price, '分（自定义）')
--         ELSE CONCAT(a.default_price, '分（默认）')
--     END AS '价格'
-- FROM platform_client_api ca
-- JOIN platform_api a ON ca.api_id = a.id
-- WHERE ca.client_id = 'client_demo_001'
--   AND ca.status = 1
--   AND a.status = 1;

-- 5. 验证权限（示例：client_demo_002 可以访问哪些 API）
-- SELECT 
--     a.api_code AS '权限标识',
--     a.api_name AS 'API名称',
--     a.api_path AS 'API路径',
--     a.http_method AS '方法',
--     CONCAT(a.default_price, '分') AS '价格'
-- FROM platform_client_api ca
-- JOIN platform_api a ON ca.api_id = a.id
-- WHERE ca.client_id = 'client_demo_002'
--   AND ca.status = 1
--   AND a.status = 1;
