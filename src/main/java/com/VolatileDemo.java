package com;

public class VolatileDemo {
    static class Processor {
        //without volatile program keeps running on my platform
        private boolean flag = false;

        public void setFlag() {
            System.out.println("setting flag true" +
                    "");
            this.flag = true;
        }

        public void process() {
            while(!flag) {
                int x = 5;
                // using sleep or sout will end the program without volatile.
                // Probably these operations, cause thread to be rescheduled, read from memory. Thus read new flag value and end.
            }

            System.out.println("Ending");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Processor processor = new Processor();
        Thread t1 = new Thread(processor::process);

        t1.start();

        Thread.sleep(2000);
        processor.setFlag();

    }
}


