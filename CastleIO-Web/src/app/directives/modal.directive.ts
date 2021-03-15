import {Directive, ElementRef, OnInit, Renderer2} from '@angular/core';

@Directive({
    selector: '[appModal]',
})
export class ModalDirective implements OnInit {

    constructor(private renderer: Renderer2, private elementRef: ElementRef) {
    }

    ngOnInit(): void {
        const curtain = document.createElement('div');
        const nativeElement = this.elementRef.nativeElement;
        console.log(nativeElement);
        curtain.className = 'curtain';
        this.renderer.insertBefore(
            nativeElement.parentElement,
            curtain,
            nativeElement);
    }
}
