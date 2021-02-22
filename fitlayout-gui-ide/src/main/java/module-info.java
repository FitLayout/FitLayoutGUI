module cz.vutbr.fit.layout.ide 
{
    requires org.slf4j;
    
    requires java.desktop;
    requires rdf4j.onejar;
    requires com.google.gson;
    
    requires transitive cz.vutbr.fit.layout.core;
    requires cz.vutbr.fit.layout.io;
    requires cz.vutbr.fit.layout.segm;
    requires cz.vutbr.fit.layout.vips;
    requires cz.vutbr.fit.layout.bcs;
    requires cz.vutbr.fit.layout.rdf;
    
    exports cz.vutbr.fit.layout.ide.config;
    exports cz.vutbr.fit.layout.ide.service;
}
