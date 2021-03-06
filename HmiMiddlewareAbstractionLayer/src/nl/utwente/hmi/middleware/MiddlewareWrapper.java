package nl.utwente.hmi.middleware;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.utwente.hmi.middleware.helpers.JsonNodeBuilders;
import nl.utwente.hmi.middleware.loader.GenericMiddlewareLoader;
import nl.utwente.hmi.middleware.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static nl.utwente.hmi.middleware.helpers.JsonNodeBuilders.object;

/**
 * The MiddlewareWrapper is designed to have a simple implementation for creating a middleware with a listener
 * (MiddlewareListener) for receiving data and a worker (Worker) for processing data. If more flexibility is required,
 * the author can utilize the Worker and MiddlewareListener interfaces. Additionally, this class contains methods
 * for sending data as well, next to methods for retrieving data.
 * @author WaterschootJB
 */

public abstract class MiddlewareWrapper implements Worker, MiddlewareListener{

    private ObjectMapper mapper;
    private boolean running = true;
    private static Logger logger = LoggerFactory.getLogger(MiddlewareWrapper.class.getName());
    private BlockingQueue<JsonNode> queue;
    private Middleware middleware;

    /**
     * Constructor for instantiating a thread with an abstracted middleware wrapper using a
     * LinkedBlockingQueue. This wrapper has two separate threads for retrieving data (itself) from
     * middleware and processing the received data (a worker thread).
     * @param middleware, the middleware being used for creating the wrapper
     */
    public MiddlewareWrapper(Middleware middleware){
        this.middleware = middleware;
        this.mapper = new ObjectMapper();
        middleware.addListener(this);
        logger.debug("Listener created");
        this.queue = new LinkedBlockingQueue<>();
        new Thread(this).start();

    }

    /**
     * Constructor for the middleware wrapper that requires properties, convenient if you don't want to
     * use files for configuration.
     * @param ps, properties that require the following properties
     *            "middleware", e.g. middleware: nl.utwente.hmi.middleware.activemq.ActiveMQMiddleWare
     *            "address of middleware", e.g. amqBrokerURI: tcp://localhost:61616
     *            "other required properties of the specific middleware", e.g. iTopic:in and oTopic:out
     */
    public MiddlewareWrapper(Properties ps){
        GenericMiddlewareLoader.setGlobalProperties(ps);
        GenericMiddlewareLoader gml = new GenericMiddlewareLoader(ps.getProperty("middleware"), ps);
        this.middleware = gml.load();
        this.mapper = new ObjectMapper();
        middleware.addListener(this);
        logger.debug("Listener created");
        this.queue = new LinkedBlockingQueue<>();
        new Thread(this).start();
    }


    /**
     * Constructor for the middleware wrapper that requires two property file names. The wrapper has two
     * separate threads for retrieving data (itself) and processing data (a worker thread).
     * @param generalProps, property file containing the connecting link for the middleware
     * @param specificProps, property file containing the middleware instantiation and its required properties
     */
    public MiddlewareWrapper(String generalProps, String specificProps){
        Properties ps = new Properties();
        InputStream mwProps = MiddlewareWrapper.class.getClassLoader().getResourceAsStream(specificProps);
        try {
            ps.load(mwProps);
        } catch (IOException e) {
            logger.warn("Could not load flipper middleware props file {}", mwProps);
            e.printStackTrace();
        }
        GenericMiddlewareLoader.setGlobalPropertiesFile(generalProps);
        GenericMiddlewareLoader gml = new GenericMiddlewareLoader(ps.getProperty("middleware"), ps);
        this.middleware = gml.load();
        this.mapper = new ObjectMapper();
        middleware.addListener(this);
        logger.debug("Listener created");
        this.queue = new LinkedBlockingQueue<>();
        new Thread(this).start();
    }

    /**
     * Does the required processing on the data
     * Each worker is responsible for implementing this method as required.
     * @param jn the incoming data in JsonNode format
     */
    public abstract void processData(JsonNode jn);

    @Override
    public void addDataToQueue(JsonNode jn) {
        queue.add(jn);
    }

    @Override
    public void run() {
        logger.debug("Worker started.");
        while(running){
            try {
                JsonNode jn = queue.take();
                processData(jn);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if new data is present in the queue
     * @return true if there is new data
     */
    public boolean hasMessage(){
        return !queue.isEmpty();
    }

    /**
     * Retrieves the data from the middleware
     * @return the data
     */
    public JsonNode getMessage() {
        logger.debug("Retrieving message: {}",queue.peek());
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mapper.createObjectNode();
    }

    /**
     * Returns if the middleware is created and connected
     * @return true if connected, false if not
     */
    public boolean isConnected() {
        return middleware != null;
    }


    public void receiveData(JsonNode jn){
        logger.debug("Received data: {}", jn.toString());
        queue.clear();
        queue.add(jn);
    }

    /**
     * Sends data in UTF-8 format over the middleware, which is of form { "content" : $data}
     * @param data, a String representation of the data to send
     */
    public void sendData(String data) {
        JsonNodeBuilders.ObjectNodeBuilder on = object();
        try {
            on.with("content", URLEncoder.encode(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        logger.debug("Sending data: {}",data);
        middleware.sendData(on.end());
    }
}
