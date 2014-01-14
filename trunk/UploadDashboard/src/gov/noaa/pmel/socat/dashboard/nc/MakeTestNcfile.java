package gov.noaa.pmel.socat.dashboard.nc;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MakeTestNcfile {
    static Random rand = new Random();
    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            List<SocatCruiseData> data = new ArrayList<SocatCruiseData>();
            for ( int i = 0; i < 10; i++ ) {
                SocatCruiseData cruise = new SocatCruiseData();
                Class<?> clazz = cruise.getClass();
                Field[] fields = clazz.getDeclaredFields();
                for (int j = 0; j < fields.length; j++) {
                    Field field = fields[j];
                    field.setAccessible(true);
                    Class<?> flazz = field.getType();

                    if ( flazz.equals(Double.class) || flazz.equals(Double.TYPE) ) {
                        try {
                            field.set(cruise, Double.valueOf(rand.nextDouble()));
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if ( flazz.equals(String.class) ) {
                        try {
                            String bling = "A string with a random number "+String.valueOf(rand.nextDouble());
                            field.set(cruise, bling);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if ( flazz.equals(Integer.class) || flazz.equals(Integer.TYPE) ) {
                        try {
                            field.set(cruise, new Integer(rand.nextInt(10000)) );
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else if ( flazz.equals(Long.class) || flazz.equals(Long.TYPE) ) {
                        if ( !field.getName().equals("serialVersionUID") ) {
                            try {
                                field.set(cruise,  rand.nextInt(10000));
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    } else if ( flazz.equals(Date.class) ) {
                        try {
                            field.set(cruise, new Date());
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                data.add(cruise);

            }
            SocatMetadata metadata = new SocatMetadata();
            Class<?> clazz = metadata.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (int j = 0; j < fields.length; j++) {
                Field field = fields[j];
                field.setAccessible(true);
                Class<?> flazz = field.getType();

                if ( flazz.equals(Double.class) || flazz.equals(Double.TYPE) ) {
                    try {
                        field.set(metadata, rand.nextDouble());
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if ( flazz.equals(String.class) ) {
                    try {
                        String bling = "A string with a random number "+String.valueOf(rand.nextDouble());
                        field.set(metadata, bling);
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if ( flazz.equals(Integer.class) || flazz.equals(Integer.TYPE) ) {
                    try {
                        field.set(metadata, rand.nextInt(10000) );
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if ( flazz.equals(Long.class) || flazz.equals(Long.TYPE) ) {
                    if ( !field.getName().equals("serialVersionUID") ) {
                        try {
                            field.set(metadata, rand.nextInt(10000) );
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } else if ( flazz.equals(Date.class) ) {
                    try {
                        field.set(metadata, new Date());
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            CruiseSdgncFile ncfile = new CruiseSdgncFile(metadata, data);

            ncfile.create("testDSG.nc");
            ncfile.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void loadRandom(Class<?> clazz) throws IntrospectionException {

    }
}
