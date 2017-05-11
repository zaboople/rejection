package main.swang;

/**
 * This is a relatively unimportant abstraction that describes Screen's view of ScreenPlay:
 * I don't know who you are, but you do these three things. Screen is less interested in talking
 * back than listening, however.
 */
interface ScreenPlayInterface {
  default void init(Screen screen){}
  default void betEntered(String bet){}
  default void moveEntered(String move){}
}