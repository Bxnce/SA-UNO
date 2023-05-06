package controller


import controller.controllerComponent.ControllerAPI
import controller.RestModule.given_controllerInterface

object RunCore {
  @main def run =
    ControllerAPI().start()
}