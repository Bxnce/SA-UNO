package controller


import controller.controllerComponent.ControllerAPI
import controller.RestModule.given_controllerInterface

object Rest {
  @main def run =
    ControllerAPI().start()
}