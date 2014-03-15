<jboss-deployment-structure>
    <deployment>
        <exclusions>
           <module name="org.jboss.resteasy.resteasy-jackson-provider"/>
        </exclusions>
        <dependencies>
            <module name="org.jboss.resteasy.resteasy-jackson2-provider" services="import"/>
            <module name="javax.ws.rs.api"/>
        </dependencies>
    </deployment>
</jboss-deployment-structure> 