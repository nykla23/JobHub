根据接口文档设计四五六七部分的模块的接口，在config添加MyBatisPlusConfig；
controller、添加Group、Home、Search、Topic；
dto添加ChatRoom、Group、Home、Search、Topic；
entity添加Group、Home、Topic、Carousel、HotActivity；
mapper添加ChatRoom、groups、Topic、Carousel、HotActivity、Search、Home相关mapper；
service添加Group、Home、Search、Topic及其实现；
在pom.xml文件中的<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.30</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
添加了<version>1.18.30</version>
在application.yml文件中修改spring.datasource.jpa.hibernate:ddl-auto: update //使用 update 模式，自动创建或更新表结构
数据库中新创建没有的表——carousel
groups 是 MySQL 的保留关键字，不能直接用作表名。—— 在Group实体类添加反引号 @Table(name = "`groups`")
## 这一部分未做相应修改，后续会。。。——测试接口返回显示：拒绝访问 localhost —— Spring Security 拦截了请求：从日志可以看到应用使用了 Spring Security：Global AuthenticationManager configured with UserDetailsService bean with name customUserDetailsService
因为上一条的原因，接口还未做相应测试。。。
