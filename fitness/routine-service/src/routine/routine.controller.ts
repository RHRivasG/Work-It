import {
  Body,
  Controller,
  Get,
  Param,
  Post,
  UseGuards,
  Request,
  UnauthorizedException,
  Put,
  Delete,
} from '@nestjs/common';
import { JwtAuthGuard } from 'src/auth/jwt-auth.guard';
import {
  RoutineDto,
  RoutineService,
  CreateRoutine,
  UpdateRoutine,
  DeleteRoutine,
  AddTraining,
  RemoveTraining,
} from '../application';

class RoutineCreationForm {
  name: string;
  description: string;
  trainings: string[];
}

class RoutineUpdatingForm {
  name: string;
  description: string;
  trainings: string[];
}

@Controller('routines')
export class RoutineController {
  constructor(private readonly _routineService: RoutineService) {}

  @UseGuards(JwtAuthGuard)
  @Get()
  async getRoutines(@Request() req: any): Promise<RoutineDto[]> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    return await this._routineService.getAll(user.userId);
  }

  @UseGuards(JwtAuthGuard)
  @Get(':id')
  async getRoutine(
    @Param('id') id: string,
    @Request() req: any,
  ): Promise<RoutineDto> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    return await this._routineService.get(id);
  }

  @UseGuards(JwtAuthGuard)
  @Post()
  async create(
    @Request() req: any,
    @Body() routine: RoutineCreationForm,
  ): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: CreateRoutine = new CreateRoutine(
      routine.name,
      routine.description,
      user.userId,
      routine.trainings,
    );
    command.execute(this._routineService);
  }

  @UseGuards(JwtAuthGuard)
  @Put(':id')
  async update(
    @Param('id') id: string,
    @Request() req: any,
    @Body() routine: RoutineUpdatingForm,
  ): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: UpdateRoutine = new UpdateRoutine(
      id,
      routine.name,
      routine.description,
      user.userId,
      routine.trainings,
    );
    command.execute(this._routineService);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id')
  async delete(@Param('id') id: string, @Request() req: any): Promise<void> {
    const user = req.user;
    if (!user.roles.includes('participant')) {
      throw new UnauthorizedException();
    }
    const command: DeleteRoutine = new DeleteRoutine(id);
    command.execute(this._routineService);
  }

  @UseGuards(JwtAuthGuard)
  @Post(':id/training/:idt')
  async addTraining(
    @Param('id') id: string,
    @Param('idt') idt: string,
  ): Promise<void> {
    const command: AddTraining = new AddTraining(id, idt);
    command.execute(this._routineService);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id/training/:idt')
  async removeTraining(
    @Param('id') id: string,
    @Param('idt') idt: string,
  ): Promise<void> {
    const command: RemoveTraining = new RemoveTraining(id, idt);
    command.execute(this._routineService);
  }
}
