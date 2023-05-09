package controller


import controller.controllerComponent.ControllerAPI
import controller.RestModule.given_controllerInterface

object CoreMain {
  @main def run =
    ControllerAPI().start()
}